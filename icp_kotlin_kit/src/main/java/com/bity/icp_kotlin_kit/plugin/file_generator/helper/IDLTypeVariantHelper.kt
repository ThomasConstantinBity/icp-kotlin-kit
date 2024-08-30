package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVariantParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFuncDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.file_generator.KotlinCommentGenerator
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeCustomHelper
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeRecordHelper

internal object IDLTypeVariantHelper {

    fun typeVariantToKotlinClass(
        className: String,
        typeVariant: IDLTypeVariant
    ): String {

        val kotlinClassString = StringBuilder().appendLine("sealed class $className {")

        val idlVariantDeclaration = CandidVariantParser.parseVariant(typeVariant.variantDeclaration)
        idlVariantDeclaration.variants.forEach {

            // Comment
            it.comment?.let { comment ->
                kotlinClassString.append(KotlinCommentGenerator.getKotlinComment(comment))
            }

            var additionalClassDeclaration = ""
            val kotlinClassDefinition = when(val type = it.type) {

                is IDLTypeRecord -> {
                    IDLTypeRecordHelper.typeRecordToKotlinClass(
                        className = it.id ?: TODO(),
                        type = type
                    )
                }

                is IDLTypeNull -> "data object ${it.id}"

                /**
                 * We need to declare a single variable inside the class:
                 *
                 * type QueryArchiveResult = variant {
                 *     Ok : BlockRange;
                 *     Err : null;
                 * };
                 *
                 * class Ok(val blockRange: BlockRange): QueryArchiveResult()
                 */
                is IDLTypeCustom -> {
                    it.id?.let { cn ->
                        IDLTypeCustomHelper.idlTypeCustomToKotlinClass(
                            className = cn,
                            idlTypeCustom = type
                        )
                    } ?: "data object ${type.typeDef}"
                }

                else -> {
                    additionalClassDeclaration = getAdditionalClassDeclaration(type)
                    primitiveTypeToDataClass(type)
                }
            }

            kotlinClassString.append("$kotlinClassDefinition : ${className}()")
            if(additionalClassDeclaration.isNotEmpty())
                kotlinClassString.appendLine("{\n$additionalClassDeclaration\n}")
            else kotlinClassString.appendLine()
        }

        kotlinClassString.append("}")
        return kotlinClassString.toString()
    }

    // TODO, can we return also a data object?
    private fun primitiveTypeToDataClass(idlType: IDLType): String =
        when(idlType) {
            is IDLFun -> TODO()
            is IDLTypeBlob -> "data class Blob(val data: ByteArray)"
            is IDLTypeBoolean -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeInt -> "data class Int(val value: BigInteger)"
            is IDLTypeNat -> "data class Nat(val value: BigInteger)"
            is IDLTypeNat64 -> TODO()
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> TODO()
            is IDLTypeText -> "data class Text(val text: String)"
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> {
                val vecDeclaration = CandidVecParser.parseVec(idlType.vecDeclaration)
                val type = vecDeclaration.type
                if(type is IDLTypeRecord)
                    "data class Map(val elements: List<Elements>)"
                else
                    "data class Array(val elements: List<Value>)"
            }
        }

    private fun getAdditionalClassDeclaration(idlType: IDLType): String {
        return when(idlType) {

            is IDLTypeVec -> {
                val vecDeclaration = CandidVecParser.parseVec(idlType.vecDeclaration)
                val type = vecDeclaration.type
                return IDLTypeHelper.idlTypeToKotlinClass(
                    className = "Elements",
                    idlType = type
                )
            }

            is IDLTypeBlob -> """
                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (javaClass != other?.javaClass) return false
        
                    other as Blob
        
                    return data.contentEquals(other.data)
                }
        
                override fun hashCode(): kotlin.Int {
                    return data.contentHashCode()
                }
            """.trimIndent()

            else -> ""
        }
    }
}