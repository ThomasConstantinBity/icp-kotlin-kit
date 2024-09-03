package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidTypeParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.enum.KotlinClassDefinitionType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFuncDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeRecordHelper.typeRecordToKotlinClass
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeVariantHelper.typeVariantToKotlinClass
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeVecHelper.typeVecToKotlinDefinition
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinClassFinder.Result.KotlinClass

internal object IDLTypeDeclarationConverter {

    operator fun invoke(input: String, className: String? = null): KotlinClassDefinition {
        val kotlinString = StringBuilder()
        val idlTypeDeclaration = CandidTypeParser.parseType(input)

        // Comment
        idlTypeDeclaration.comment?.let {
            kotlinString.appendLine(KotlinCommentGenerator.getKotlinComment(it))
        }

        val definitionName: String
        val kotlinDefinition: String
        val classDefinitionType: KotlinClassDefinitionType

        when(val type = idlTypeDeclaration.type) {
            is IDLFun -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> {
                definitionName = idlTypeDeclaration.id
                kotlinDefinition = KotlinFunctionGenerator(
                    className = className,
                    funId = idlTypeDeclaration.id,
                    idlTypeFunc = type
                )
                classDefinitionType = KotlinClassDefinitionType.Function
            }
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> {
                definitionName = idlTypeDeclaration.id
                kotlinDefinition = typeRecordToKotlinClass(
                    className = idlTypeDeclaration.id,
                    type = type
                )
                classDefinitionType = KotlinClassDefinitionType.Class
            }
            is IDLTypeVariant -> {
                definitionName = idlTypeDeclaration.id
                kotlinDefinition = typeVariantToKotlinClass(
                    className = idlTypeDeclaration.id,
                    typeVariant = type
                )
                classDefinitionType = KotlinClassDefinitionType.SealedClass
            }
            is IDLTypeVec -> {
                definitionName = idlTypeDeclaration.id
                kotlinDefinition = typealiasDefinition(
                    id = idlTypeDeclaration.id,
                    kotlinType = typeVecToKotlinDefinition(type, className)
                )
                classDefinitionType = KotlinClassDefinitionType.Array
            }
            else -> {
                definitionName = idlTypeDeclaration.id
                kotlinDefinition = typealiasDefinition(
                    id = idlTypeDeclaration.id,
                    kotlinType = IDLTypeHelper.kotlinTypeVariable(type, className)
                )
                classDefinitionType = KotlinClassDefinitionType.TypeAlias
            }
        }
        kotlinString.appendLine(kotlinDefinition)
        return KotlinClassDefinition(
            definitionName = definitionName,
            candidDefinition = input,
            kotlinDefinition = kotlinString.toString(),
            classDefinitionType = classDefinitionType
        )
    }

    private fun typealiasDefinition(id: String, kotlinType: String) = "typealias $id = $kotlinType"
}