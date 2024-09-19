package com.bity.icp_kotlin_kit.candid.serializer

import com.bity.icp_kotlin_kit.candid.model.CandidType
import com.bity.icp_kotlin_kit.cryptography.LEB128
import com.bity.icp_kotlin_kit.util.ext_function.joinedData

internal class CandidTypeTable {
    private var customTypes = mutableListOf<CandidTypeData>()

    fun getReference(type: CandidType): Int =
        when(type) {
            is CandidType.Vector -> {
                val typeData = CandidTypeData(
                    types = listOf(
                        CandidTypeData.EncodableType.Signed(type.primitiveType.value),
                        CandidTypeData.EncodableType.Signed(getReference(type.candidType))
                    )
                )
                addOrFind(typeData)
            }

            is CandidType.Option -> {
                val typeData = CandidTypeData(
                    types = listOf(
                        CandidTypeData.EncodableType.Signed(type.primitiveType.value),
                        CandidTypeData.EncodableType.Signed(getReference(type.candidType))
                    )
                )
                addOrFind(typeData)
            }

            is CandidType.Record -> {
                val typeData = CandidTypeData(
                    types = listOf(
                        CandidTypeData.EncodableType.Signed(type.primitiveType.value),
                        CandidTypeData.EncodableType.Unsigned(type.candidKeyedTypes.size.toULong())
                    ) + type.candidKeyedTypes.items.flatMap {
                        listOf(
                            CandidTypeData.EncodableType.Unsigned(it.key.longValue.toULong()),
                            CandidTypeData.EncodableType.Signed(getReference(it.type))
                        )
                    }
                )
                addOrFind(typeData)
            }

            is CandidType.Variant -> {
                val typeData = CandidTypeData(
                    types = listOf(
                        CandidTypeData.EncodableType.Signed(type.primitiveType.value),
                        CandidTypeData.EncodableType.Unsigned(type.candidKeyedTypes.size.toULong())
                    ) + type.candidKeyedTypes.items.flatMap {
                        listOf(
                            CandidTypeData.EncodableType.Unsigned(it.key.longValue.toULong()),
                            CandidTypeData.EncodableType.Signed(getReference(it.type))
                        )
                    }
                )
                addOrFind(typeData)
            }

            is CandidType.Function -> TODO()
            CandidType.Bool -> TODO()
            CandidType.Empty -> TODO()
            CandidType.Float32 -> TODO()
            CandidType.Float64 -> TODO()
            CandidType.Integer -> TODO()
            CandidType.Integer16 -> TODO()
            CandidType.Integer32 -> TODO()
            CandidType.Integer64 -> TODO()
            CandidType.Integer8 -> TODO()
            is CandidType.Named -> TODO()
            CandidType.Natural -> TODO()
            CandidType.Natural16 -> TODO()
            CandidType.Natural32 -> TODO()
            CandidType.Natural64 -> TODO()
            CandidType.Natural8 -> TODO()
            CandidType.Null -> TODO()
            CandidType.Principal -> TODO()

            CandidType.Reserved -> TODO()
            is CandidType.Service -> TODO()
            CandidType.Text -> TODO()
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