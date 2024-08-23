package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidRecordParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVariantParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_record.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFunc
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec

internal object KotlinClassGenerator {

    fun kotlinClass(idlTypeDeclaration: IDLTypeDeclaration): String {
        val kotlinClass = StringBuilder()

        // Comment
        idlTypeDeclaration.comment?.let {
            kotlinClass.append(KotlinCommentGenerator.getKotlinComment(it))
        }

        val definition = when(val type =idlTypeDeclaration.type) {
            is IDLTypeCustom -> TODO()
            is IDLTypeFunc -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> """
                class ${idlTypeDeclaration.id} (
                    ${typeRecordToKotlinClass(type)}
                )
            """.trimIndent()
            is IDLTypeVariant -> """
                sealed class ${idlTypeDeclaration.id} {
                	${typeVariantToKotlinClass(type, idlTypeDeclaration.id)}
                }
            """.trimIndent()
            else -> typealiasDefinition(idlTypeDeclaration)
        }

        kotlinClass.append(definition)
        return kotlinClass.toString()
    }

    private fun typealiasDefinition(typeDeclaration: IDLTypeDeclaration) =
        "typealias ${typeDeclaration.id} = ${getCorrespondingKotlinClass(typeDeclaration.type)}" +
                if(typeDeclaration.isOptional) "?" else ""

    private fun typeRecordToKotlinClass(recordType: IDLTypeRecord): String {
        val classDefinition = StringBuilder()
        val varDefinitions = CandidRecordParser.parseRecord(recordType.recordDeclaration)
            .records
            .joinToString(",\n") {
                idlRecordToClassVariable(it)
            }
        classDefinition.append(varDefinitions)
        return classDefinition.toString()
    }

    private fun typeVariantToKotlinClass(
        typeVariant: IDLTypeVariant,
        parentClassName: String
    ): String {
        val classDefinition = StringBuilder()

        val classesDefinition = CandidVariantParser.parseVariant(typeVariant.variantDeclaration)
            .variants.joinToString("\n") { variant ->

                val correspondingKotlinClass = when(val type = variant.type) {
                    is IDLTypeCustom -> {
                        val clazz = type.typeDef
                        "val ${clazz.replaceFirstChar { it.lowercase() }} : $clazz"
                    }
                    else -> getCorrespondingKotlinClass(type)
                }

                if (correspondingKotlinClass != null)
                    """
                        class ${variant.id} (
                            $correspondingKotlinClass
                        ) : $parentClassName()
                    """.trimIndent()
                else "data object ${variant.id} : $parentClassName()"
            }
        classDefinition.append(classesDefinition)
        return classDefinition.toString()
    }

    private fun idlRecordToClassVariable(idlRecord: IDLRecord): String {
        val variableDeclaration = StringBuilder()
        idlRecord.comment?.let {
            variableDeclaration.append(KotlinCommentGenerator.getKotlinComment(it))
            variableDeclaration.append("\n")
        }
        variableDeclaration.append(
            """
                val ${idlRecord.id} : ${getCorrespondingKotlinClass(idlRecord.type)}${if (idlRecord.isOptional) "?" else ""}
            """.trimIndent()
        )
        return variableDeclaration.toString()
    }

    private fun getCorrespondingKotlinClass(idlType: IDLType): String? =
        when(idlType) {
            is IDLTypeBlob -> "ByteArray"
            is IDLTypeBoolean -> TODO()
            is IDLTypeCustom -> idlType.typeDef
            is IDLTypeFunc -> TODO()
            is IDLTypeInt -> TODO()
            is IDLTypeNat -> TODO()
            is IDLTypeNat64 -> "ULong"
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> typeRecordToKotlinClass(idlType)
            is IDLTypeText -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeNull -> null
            is IDLTypeVec -> {
                val idlVec = CandidVecParser.parseVec(idlType.vecDeclaration)
                if(idlVec.isOptional) "Array<${getCorrespondingKotlinClass(idlVec.type)}?>"
                else "Array<${getCorrespondingKotlinClass(idlVec.type)}>"
            }
        }
}