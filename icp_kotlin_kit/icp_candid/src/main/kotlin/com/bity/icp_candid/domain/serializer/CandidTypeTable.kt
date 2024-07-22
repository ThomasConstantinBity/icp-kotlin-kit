package com.bity.icp_candid.domain.serializer

import com.bity.icp_candid.domain.model.CandidPrimitiveType
import com.bity.icp_candid.domain.model.CandidType
import com.bity.icp_candid.util.ext_function.joinedData
import com.bity.icp_cryptography.util.LEB128

class CandidTypeTable {
    private var customTypes = mutableListOf<CandidTypeData>()

    fun getReference(type: CandidType): Int =
        when(type) {
            is CandidType.Primitive -> type.primitiveType.value
            is CandidType.Container -> {
                val typeData = CandidTypeData(
                    types = listOf(
                        CandidTypeData.EncodableType.Signed(type.primitiveType.value),
                        CandidTypeData.EncodableType.Signed(getReference(type.type))
                    )
                )
                addOrFind(typeData)
            }
            is CandidType.Function -> {
                val typeData = mutableListOf<CandidTypeData.EncodableType>()
                typeData.apply {
                    add(CandidTypeData.EncodableType.Signed(CandidPrimitiveType.FUNCTION.value))
                    add(CandidTypeData.EncodableType.Unsigned(type.signature.inputs.size.toULong()))
                    addAll(type.signature.inputs.map { CandidTypeData.EncodableType.Signed(getReference(it)) })
                    add(CandidTypeData.EncodableType.Unsigned(type.signature.outputs.size.toULong()))
                    addAll(type.signature.outputs.map { CandidTypeData.EncodableType.Signed(getReference(it)) })
                }

                val annotations = mutableListOf<CandidTypeData.EncodableType>()
                if(type.signature.isQuery) {
                    annotations.add(CandidTypeData.EncodableType.Unsigned(0x01.toULong()))
                }
                if(type.signature.isOneWay) {
                    annotations.add(CandidTypeData.EncodableType.Unsigned(0x02.toULong()))
                }
                typeData.add(CandidTypeData.EncodableType.Unsigned(annotations.size.toULong()))
                typeData.addAll(annotations)
                addOrFind(
                    CandidTypeData(
                        types = typeData
                    )
                )
            }
            is CandidType.KeyedContainer -> {
                val typeData = CandidTypeData(
                    types = mutableListOf(
                        CandidTypeData.EncodableType.Signed(type.primitiveType.value),
                        CandidTypeData.EncodableType.Unsigned(type.dictionaryItemType.size.toULong()),
                    ) + type.dictionaryItemType.flatMap {
                        listOf(
                            CandidTypeData.EncodableType.Unsigned(it.hashedKey),
                            CandidTypeData.EncodableType.Signed(getReference(it.type))
                        )
                    }
                )
                addOrFind(typeData)
            }
        }

    fun encode(): ByteArray =
        LEB128.encodeUnsigned(customTypes.size) +
                customTypes.map { it.encode() }.joinedData()

    private fun addOrFind(typeData: CandidTypeData): Int {
        val index = customTypes.indexOfFirst { it == typeData }
        if(index == -1) {
            customTypes.add(typeData)
            return customTypes.size - 1
        }
        return index
    }
}