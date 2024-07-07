package com.bity.icp_candid.domain.deserializer

import com.bity.icp_cryptography.util.LEB128
import com.bity.icp_candid.domain.model.CandidDictionaryItemType
import com.bity.icp_candid.domain.model.CandidFunction
import com.bity.icp_candid.domain.model.CandidPrimitiveType
import com.bity.icp_candid.domain.model.CandidType
import com.bity.icp_candid.domain.model.error.CandidDeserializationError
import java.io.ByteArrayInputStream

class CandidDecodableTypeTable(stream: ByteArrayInputStream) {

    private val types: List<CandidType>

    init {
        val typeCount: Int = LEB128.decodeUnsigned(stream)
        val typesRawData = (0 until typeCount).map {
            CandidTypeTableData.decode(stream)
        }
        types = typesRawData.map {
            buildType(it, typesRawData)
        }
    }

    @Throws()
    private fun buildType(
        type: CandidTypeTableData,
        rawTypeData: List<CandidTypeTableData>
    ): CandidType =
        when(type) {

            is CandidTypeTableData.Container -> {
                val referencedType = candidType(type.containedType, rawTypeData)
                CandidType.Container(type.containerType, referencedType)
            }

            is CandidTypeTableData.KeyedContainer -> {
                val rowTypes = type.rows.map {
                    // Need to use ite, type
                    val rowType = candidType(it.second, rawTypeData)
                    // Need to use hashedKey
                    CandidDictionaryItemType(it.first.toULong(), rowType)
                }
                CandidType.KeyedContainer(type.containerType, rowTypes)
            }

            is CandidTypeTableData.FunctionSignature -> {
                CandidType.Function(
                    CandidFunction.CandidFunctionSignature(
                        inputs = type.inputTypes.map { candidType(it, rawTypeData) },
                        outputs = type.outputTypes.map { candidType(it, rawTypeData) },
                        isQuery = type.annotations.contains(0x01.toULong()),
                        isOneWay = type.annotations.contains(0x02.toULong())
                    )
                )
            }
        }

    @Throws(
        CandidDeserializationError.InvalidTypeReference::class,
        CandidDeserializationError.InvalidPrimitive::class
    )
    private fun candidType(type: Int, rawTypeData: List<CandidTypeTableData>): CandidType {
        return if(type >= 0) {
            require(rawTypeData.size > type) {
                throw CandidDeserializationError.InvalidTypeReference()
            }
            buildType(rawTypeData[type], rawTypeData)
        } else {
            val primitiveContainedType = CandidPrimitiveType.candidPrimitiveTypeByValue(type)
                ?: throw CandidDeserializationError.InvalidPrimitive()
            CandidType.Primitive(primitiveContainedType)
        }
    }

    @Throws(
        CandidDeserializationError.InvalidPrimitive::class,
        CandidDeserializationError.InvalidTypeReference::class
    )
    fun getTypeForReference(reference: Int): CandidType {
        if(reference < 0) {
            val primitive = CandidPrimitiveType.candidPrimitiveTypeByValue(reference)
                ?: throw CandidDeserializationError.InvalidPrimitive()
            return CandidType.Primitive(primitive)
        }
        require(types.size > reference) {
            throw CandidDeserializationError.InvalidTypeReference()
        }
        return types[reference]
    }
}