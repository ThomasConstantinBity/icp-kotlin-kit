package com.bity.icp_candid.domain.deserializer

import com.bity.icp_cryptography.util.LEB128
import com.bity.icp_candid.domain.model.CandidPrimitiveType
import com.bity.icp_candid.domain.model.error.CandidDeserializationError
import java.io.ByteArrayInputStream

sealed class CandidTypeTableData {

    class Container(
        val containerType: CandidPrimitiveType,
        val containedType: Int
    ): CandidTypeTableData()

    class KeyedContainer(
        val containerType: CandidPrimitiveType,
        val rows: List<Pair<Long, Int>>  // hashedKey, type
    ): CandidTypeTableData()

    class FunctionSignature(
        val inputTypes: List<Int>,
        val outputTypes: List<Int>,
        val annotations: List<ULong>
    ): CandidTypeTableData()

    companion object {
        @Throws(CandidDeserializationError.InvalidPrimitive::class)
        fun decode(stream: ByteArrayInputStream): CandidTypeTableData {
            val candidType: Int = LEB128.decodeSigned(stream)
            val primitive = CandidPrimitiveType.candidPrimitiveTypeByValue(candidType)
                ?: throw CandidDeserializationError.InvalidPrimitive()
            return when(primitive) {

                CandidPrimitiveType.RECORD, CandidPrimitiveType.VARIANT -> {
                    val nRows: Int = LEB128.decodeUnsigned(stream)
                    val rows: List<Pair<Long, Int>> = (0 until nRows).map {
                        // HashedKey, Type
                        Pair(LEB128.decodeUnsigned(stream), LEB128.decodeSigned(stream))
                    }
                    KeyedContainer(primitive, rows)
                }

                CandidPrimitiveType.FUNCTION -> {
                    val nInputs: Int = LEB128.decodeUnsigned(stream)
                    val inputTypes: List<Int> = (0 until nInputs).map {
                        LEB128.decodeSigned(stream)
                    }
                    val nOutputs: Int = LEB128.decodeUnsigned(stream)
                    val outputTypes: List<Int> = (0 until nOutputs).map {
                        LEB128.decodeSigned(stream)
                    }
                    val nAnnotations: Int = LEB128.decodeUnsigned(stream)
                    val annotations: List<ULong> = (0 until nAnnotations).map {
                        LEB128.decodeUnsigned(stream)
                    }
                    FunctionSignature(
                        inputTypes,
                        outputTypes,
                        annotations
                    )
                }

                else -> {
                    // all other types have a single contained type, either primitive or ref
                    val containedType: Int = LEB128.decodeSigned(stream)
                    Container(
                        containerType = primitive,
                        containedType = containedType
                    )
                }
            }
        }
    }
}