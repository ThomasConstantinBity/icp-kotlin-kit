package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidRecordParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidTypeParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVariantParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinTypeDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinitionType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassParameter
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

internal object IDLTypeDeclarationConverter {

    operator fun invoke(input: String, fileName: String): KotlinTypeDefinition {

        val idlTypeDeclaration = CandidTypeParser.parseType(input)
        val comment = KotlinCommentGenerator.getNullableKotlinComment(idlTypeDeclaration.comment)
        val classDefinitionType = getClassDefinition(
            className = idlTypeDeclaration.id,
            parentClassName = fileName,
            type = idlTypeDeclaration.type
        )
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

    private fun getClassDefinition(
        className: String,
        parentClassName: String?,
        type: IDLType
    ): KotlinClassDefinitionType {
        return when(type) {
            is IDLFun -> TODO()

            is IDLTypeText,
            is IDLTypeInt,
            is IDLTypeNat,
            is IDLTypeNat64,
            is IDLTypeBlob -> KotlinClassDefinitionType.TypeAlias(
                typeAliasId = className,
                className = parentClassName,
                type = type
            )

            is IDLTypeBoolean -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> TODO()

            is IDLTypeRecord -> convertIDLTypeRecord(
                idlType = type,
                className = className,
            )
            is IDLTypeVariant -> convertIDLTypeVariant(
                sealedClassName = className,
                idlTypeVariant = type
            )
            is IDLTypeVec -> TODO()
        }
    }

    private fun convertIDLTypeRecord(
        idlType: IDLTypeRecord,
        className: String,
    ): KotlinClassDefinitionType {
        val idlRecordDeclaration = CandidRecordParser.parseRecord(idlType.recordDeclaration)
        val classParameters = idlRecordDeclaration.records.map {
            KotlinClassParameter(
                comment = KotlinCommentGenerator.getNullableKotlinComment(it.comment),
                id = it.id,
                type = it.type,
                isOptional = it.isOptional
            )
        }
        return KotlinClassDefinitionType.Class(
            className = className,
            params = classParameters,
        )
    }

    private fun convertIDLTypeVariant(
        sealedClassName: String,
        idlTypeVariant: IDLTypeVariant
    ): KotlinClassDefinitionType {
        val idlVariantDeclaration = CandidVariantParser.parseVariant(idlTypeVariant.variantDeclaration)
        val sealedClass = KotlinClassDefinitionType.SealedClass(sealedClassName)
        val classes = idlVariantDeclaration.variants.map {
            val clasDefinition = getClassDefinition(
                parentClassName = null,
                className = it.id ?: TODO(),
                type = it.type
            )
            when(clasDefinition) {
                is KotlinClassDefinitionType.Class -> clasDefinition.inheritedClass = sealedClass
                is KotlinClassDefinitionType.Object -> clasDefinition.inheritedClass = sealedClass
                is KotlinClassDefinitionType.SealedClass -> clasDefinition.inheritedClass = sealedClass
                else -> { }
            }
            clasDefinition
        }
        sealedClass.innerClasses.addAll(classes)
        return sealedClass
    }
}