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
            is IDLTypeBlob -> typealiasDefinition(idlTypeDeclaration.id, "ByteArray")
            is IDLTypeBoolean -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeInt -> TODO()
            is IDLTypeNat -> TODO()
            is IDLTypeNat64 -> TODO()
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord ->
                typeRecordToKotlinClass(
                    className = idlTypeDeclaration.id,
                    type = type
                )
            is IDLTypeText -> TODO()
            is IDLTypeVariant -> typeVariantToKotlinClass(idlTypeDeclaration)
            is IDLTypeVec -> TODO()
            is IDLTypeVecRecord -> TODO()
        }
        kotlinClassString.appendLine(classDefinition.kotlinClassString)

        return KotlinClassDefinition(
            import = classDefinition.import,
            kotlinClassString = kotlinClassString.toString()
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
                else -> TODO()
            }
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
        val variablesDeclaration = idlRecordDeclaration.records.map {
            val kotlinClassDefinition = idlRecordToKotlinVariable(it)
            import.addAll(kotlinClassDefinition.import)
            kotlinClassDefinition.kotlinClassString
        }.joinToString(",\n")
        kotlinClassString.appendLine(variablesDeclaration)

        kotlinClassString.append(")")
        return KotlinClassDefinition(
            import = import,
            kotlinClassString = kotlinClassString.toString()
        )
    }

    private fun typeRecordToKotlinClass(recordType: IDLTypeRecord): KotlinClassDefinition {
        /*val classDefinition = StringBuilder()
        val varDefinitions = CandidRecordParser.parseRecord(recordType.recordDeclaration)
            .records
            .joinToString(",\n") {
                idlRecordToClassVariable(it)
            }
        classDefinition.append(varDefinitions)
        return classDefinition.toString()*/
        TODO()
    }

    /**
     * Type Record
     */
    private fun idlRecordToKotlinVariable(idlRecord: IDLRecord): KotlinClassDefinition {
        val kotlinVariable = StringBuilder()

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
            is IDLTypeInt -> TODO()
            is IDLTypeNat -> TODO()
            is IDLTypeNat64 -> "ULong"
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> TODO()
            is IDLTypeText -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> TODO()
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

    fun kotlinClass(idlTypeDeclaration: IDLTypeDeclaration): String {
        val kotlinClass = StringBuilder()

        // Comment
        idlTypeDeclaration.comment?.let {
            kotlinClass.append(KotlinCommentGenerator.getKotlinComment(it))
        }

        val definition = when(val type =idlTypeDeclaration.type) {
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> KotlinFunctionGenerator(
                funId = idlTypeDeclaration.id,
                idlTypeFunc = type
            )
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

    /*private fun typeRecordToKotlinClass(recordType: IDLTypeRecord): String {
        val classDefinition = StringBuilder()
        val varDefinitions = CandidRecordParser.parseRecord(recordType.recordDeclaration)
            .records
            .joinToString(",\n") {
                idlRecordToClassVariable(it)
            }
        classDefinition.append(varDefinitions)
        return classDefinition.toString()
    }*/

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
        // TODO, need to move optional in function call
        variableDeclaration.append(
            """
                val ${idlRecord.id} : ${getCorrespondingKotlinClass(idlRecord.type)}${if (idlRecord.isOptional) "?" else ""}
            """.trimIndent()
        )
        return variableDeclaration.toString()
    }

    // TODO, remove null return and throw Error?
    internal fun getCorrespondingKotlinClass(idlType: IDLType): KotlinClassDefinition? {
        val kotlinClass = when(idlType) {
            is IDLTypeBlob -> "ByteArray"
            is IDLTypeBoolean -> TODO()
            is IDLTypeCustom -> idlType.typeDef
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeInt -> "Int"
            is IDLTypeNat -> "UInt"
            is IDLTypeNat64 -> "ULong"
            is IDLTypePrincipal -> "ICPPrincipal"
            is IDLTypeRecord -> TODO() // typeRecordToKotlinClass(idlType)
            is IDLTypeText -> "String"
            is IDLTypeVariant -> TODO()
            is IDLTypeNull -> null
            is IDLTypeVec -> {
                val idlVec = CandidVecParser.parseVec(idlType.vecDeclaration)
                if(idlVec.isOptional) "Array<${getCorrespondingKotlinClass(idlVec.type)}?>"
                else "Array<${getCorrespondingKotlinClass(idlVec.type)}>"
            }
            is IDLFun -> KotlinFunctionGenerator.invoke(idlType)
            is IDLTypeVecRecord -> "Array<TODO()>"
        }
        return KotlinClassDefinition(
            kotlinClassString = kotlinClass!!
        )
    }
}