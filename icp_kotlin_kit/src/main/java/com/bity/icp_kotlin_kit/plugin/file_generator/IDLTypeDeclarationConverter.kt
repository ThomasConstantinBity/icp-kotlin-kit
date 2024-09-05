package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidRecordParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidTypeParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinTypeDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinitionType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassParameter
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
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

internal object IDLTypeDeclarationConverter {

    private fun convertIDLTypeRecordIntoClass(
        idlType: IDLTypeRecord,
        className: String
    ): KotlinClassDefinitionType {
        val idlRecordDeclaration = CandidRecordParser.parseRecord(idlType.recordDeclaration)
        val classParameters = idlRecordDeclaration.records.map {
            KotlinClassParameter(
                comment = KotlinCommentGenerator.getNullableKotlinComment(it.comment),
                id = it.id,
                type = it.type,
                isOptional = it.isOptional,
                className = className
            )
        }
        return KotlinClassDefinitionType.Class(
            className = className,
            params = classParameters
        )
    }

    operator fun invoke(input: String, className: String? = null): KotlinTypeDefinition {
        val idlTypeDeclaration = CandidTypeParser.parseType(input)

        val comment = idlTypeDeclaration.comment?.let {
            KotlinCommentGenerator.getKotlinComment(it)
        }

        val classDefinitionType = when(val type = idlTypeDeclaration.type) {
            is IDLFun -> TODO()

            is IDLTypeNat64,
            is IDLTypeBlob -> KotlinClassDefinitionType.TypeAlias(
                id = idlTypeDeclaration.id,
                className = className,
                type = type
            )

            is IDLTypeBoolean -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeInt -> TODO()
            is IDLTypeNat -> TODO()
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> convertIDLTypeRecordIntoClass(type, idlTypeDeclaration.id)
            is IDLTypeText -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> TODO()
        }

        return KotlinTypeDefinition(
            comment = comment,
            candidDefinition = input,
            classDefinitionType = classDefinitionType
        )
        /*val kotlinString = StringBuilder()
        val idlTypeDeclaration = CandidTypeParser.parseType(input)

        // Comment
        idlTypeDeclaration.comment?.let {
            kotlinString.appendLine(KotlinCommentGenerator.getKotlinComment(it))
        }

        val kotlinDefinition: String
        val classDefinitionType: KotlinClassDefinitionType

        when(val type = idlTypeDeclaration.type) {
            is IDLFun -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> {
                kotlinDefinition = KotlinFunctionGenerator(
                    className = className,
                    funId = idlTypeDeclaration.id,
                    idlTypeFunc = type
                )
                classDefinitionType = KotlinClassDefinitionType.Function
            }
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> {
                kotlinDefinition = typeRecordToKotlinClass(
                    className = idlTypeDeclaration.id,
                    type = type
                )
                classDefinitionType = KotlinClassDefinitionType.Class
            }
            is IDLTypeVariant -> {
                kotlinDefinition = typeVariantToKotlinClass(
                    className = idlTypeDeclaration.id,
                    typeVariant = type
                )
                classDefinitionType = KotlinClassDefinitionType.SealedClass
            }
            is IDLTypeVec -> {
                kotlinDefinition = typealiasDefinition(
                    id = idlTypeDeclaration.id,
                    kotlinType = typeVecToKotlinDefinition(type, className)
                )
                classDefinitionType = KotlinClassDefinitionType.Array
            }
            else -> {
                kotlinDefinition = typealiasDefinition(
                    id = idlTypeDeclaration.id,
                    kotlinType = IDLTypeHelper.kotlinTypeVariable(type, className)
                )
                classDefinitionType = KotlinClassDefinitionType.TypeAlias
            }
        }
        kotlinString.appendLine(kotlinDefinition)
        return KotlinTypeDefinition(
            candidDefinition = input,
            kotlinDefinition = kotlinString.toString(),
            classDefinitionType = classDefinitionType
        )*/
    }

    private fun typealiasDefinition(id: String, kotlinType: String) = "typealias $id = $kotlinType"
}