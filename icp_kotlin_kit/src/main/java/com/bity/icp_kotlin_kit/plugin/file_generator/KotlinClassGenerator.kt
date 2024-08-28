package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidRecordParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidTypeParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVariantParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_record.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeDeclaration
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
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVecRecord

internal class KotlinClassDefinition(
    val import: MutableSet<String> = mutableSetOf(),
    val kotlinClassString: String
)

internal object KotlinClassGenerator {

    fun kotlinClassDefinition(input: String): KotlinClassDefinition {
        val kotlinClassString = StringBuilder()
        val idlTypeDeclaration = CandidTypeParser.parseType(input)

        // Comment
        idlTypeDeclaration.comment?.let {
            kotlinClassString.appendLine(KotlinCommentGenerator.getKotlinComment(it))
        }

        val classDefinition = when(val type = idlTypeDeclaration.type) {
            is IDLFun -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord ->
                typeRecordToKotlinClass(
                    className = idlTypeDeclaration.id,
                    type = type
                )
            is IDLTypeVariant -> typeVariantToKotlinClass(idlTypeDeclaration)
            is IDLTypeVec -> typealiasDefinition(
                id = idlTypeDeclaration.id,
                kotlinType = typeVecToKotlinClass(type).kotlinClassString
            )
            is IDLTypeVecRecord -> TODO()
            else -> typealiasDefinition(
                id = idlTypeDeclaration.id,
                kotlinType = kotlinTypeVariable(type)
            )
        }
        kotlinClassString.appendLine(classDefinition.kotlinClassString)

        return KotlinClassDefinition(
            import = classDefinition.import,
            kotlinClassString = kotlinClassString.toString()
        )
    }

    private fun kotlinTypeVariable(type: IDLType): String =
        when(type) {
            is IDLFun -> TODO()
            is IDLTypeBlob -> "ByteArray"
            is IDLTypeBoolean -> "Boolean"
            is IDLTypeCustom -> type.typeDef
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeInt -> TODO()
            is IDLTypeNat -> TODO()
            is IDLTypeNat64 -> "ULong"
            is IDLTypeNull -> "Null"
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> TODO()
            is IDLTypeText -> "String"
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> TODO()
            is IDLTypeVecRecord -> TODO()
        }

    /**
     * Type Vec to Kotlin Class
     */
    private fun typeVecToKotlinClass(idlTypeVec: IDLTypeVec): KotlinClassDefinition {
        val idlVec = CandidVecParser.parseVec(idlTypeVec.vecDeclaration)
        val kotlinClass = StringBuilder("Array<${kotlinTypeVariable(idlVec.type)}>")
        if (idlVec.isOptional)
            kotlinClass.append("?")
        return KotlinClassDefinition(
            kotlinClassString = kotlinClass.toString()
        )
    }

    private fun typealiasDefinition(id: String, kotlinType: String) =
        KotlinClassDefinition(
            kotlinClassString = "typealias $id = $kotlinType"
        )

    /**
     * Type Variant to Kotlin Class
     */
    private fun typeVariantToKotlinClass(idlTypeDeclaration: IDLTypeDeclaration): KotlinClassDefinition {
        val typeVariant = idlTypeDeclaration.type
        require(typeVariant is IDLTypeVariant)

        val import: MutableSet<String> = mutableSetOf()
        val kotlinClassString = StringBuilder().appendLine("sealed class ${idlTypeDeclaration.id} {")

        val idlVariantDeclaration = CandidVariantParser.parseVariant(typeVariant.variantDeclaration)
        idlVariantDeclaration.variants.forEach {
            it.comment?.let { comment ->
                kotlinClassString.appendLine(KotlinCommentGenerator.getKotlinComment(comment))
            }
            val kotlinClassDefinition = when(val type = it.type) {
                is IDLTypeRecord -> typeRecordToKotlinClass(className = it.id, type = type)
                is IDLTypeNull -> KotlinClassDefinition(
                    kotlinClassString = "data object ${it.id}"
                )
                is IDLTypeCustom -> KotlinClassDefinition(
                    kotlinClassString = "class ${it.id}"
                )
                else -> {
                    println("type: $type")
                    KotlinClassDefinition(
                        kotlinClassString = kotlinTypeVariable(type)
                    )
                }
            }
            import.addAll(kotlinClassDefinition.import)
            kotlinClassString.appendLine("${kotlinClassDefinition.kotlinClassString} : ${idlTypeDeclaration.id}()")
        }

        kotlinClassString.append("}")
        return KotlinClassDefinition(
            import = import,
            kotlinClassString = kotlinClassString.toString())
    }

    /**
     * Type Record to Kotlin Class
     */
    private fun typeRecordToKotlinClass(className: String, type: IDLTypeRecord): KotlinClassDefinition {
        val import: MutableSet<String> = mutableSetOf()
        val kotlinClassString = StringBuilder().appendLine("class $className (")

        val idlRecordDeclaration = CandidRecordParser.parseRecord(type.recordDeclaration)
        val variablesDeclaration = idlRecordDeclaration.records.joinToString(",\n") {
            val kotlinClassDefinition = idlRecordToKotlinVariable(it)
            import.addAll(kotlinClassDefinition.import)
            kotlinClassDefinition.kotlinClassString
        }
        kotlinClassString.appendLine(variablesDeclaration)

        kotlinClassString.append(")")
        return KotlinClassDefinition(
            import = import,
            kotlinClassString = kotlinClassString.toString()
        )
    }

    /**
     * Type Record
     */
    private fun idlRecordToKotlinVariable(idlRecord: IDLRecord): KotlinClassDefinition {
        val kotlinVariable = StringBuilder()
        val import = mutableSetOf<String>()

        idlRecord.comment?.let {
            kotlinVariable.appendLine(KotlinCommentGenerator.getKotlinComment(it))
        }
        val variableDefinition = StringBuilder("val ${idlRecord.id}: ")
        val variableType = when(val type = idlRecord.type) {
            is IDLFun -> TODO()
            is IDLTypeBlob -> TODO()
            is IDLTypeBoolean -> TODO()
            is IDLTypeCustom -> type.typeDef
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeInt -> "Int"
            is IDLTypeNat -> "UInt"
            is IDLTypeNat64 -> "ULong"
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> TODO()
            is IDLTypeText -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> {
                val kotlinClassDefinition = typeVecToKotlinClass(type)
                import.addAll(kotlinClassDefinition.import)
                kotlinClassDefinition.kotlinClassString
            }
            is IDLTypeVecRecord -> TODO()
        }
        variableDefinition.append(variableType)
        if(idlRecord.isOptional)
            variableDefinition.append("?")
        kotlinVariable.append(variableDefinition)

        return KotlinClassDefinition(
            kotlinClassString = kotlinVariable.toString()
        )
    }
}