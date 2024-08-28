package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidTypeParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFuncDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVecRecord
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper.kotlinTypeVariable
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeRecordHelper.typeRecordToKotlinClass
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeVariantHelper.typeVariantToKotlinClass
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeVecHelper.typeVecToKotlinDefinition

internal object IDLTypeDeclarationConverter {

    operator fun invoke(input: String): String {
        val kotlinString = StringBuilder()
        val idlTypeDeclaration = CandidTypeParser.parseType(input)

        // Comment
        idlTypeDeclaration.comment?.let {
            kotlinString.appendLine(KotlinCommentGenerator.getKotlinComment(it))
        }

        val kotlinDefinition = when(val type = idlTypeDeclaration.type) {
            is IDLFun -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord ->
                typeRecordToKotlinClass(
                    className = idlTypeDeclaration.id,
                    type = type
                )
            is IDLTypeVariant -> typeVariantToKotlinClass(
                className = idlTypeDeclaration.id,
                typeVariant = type
            )
            is IDLTypeVec -> typealiasDefinition(
                id = idlTypeDeclaration.id,
                kotlinType = typeVecToKotlinDefinition(type)
            )
            is IDLTypeVecRecord -> TODO()
            else -> typealiasDefinition(
                id = idlTypeDeclaration.id,
                kotlinType = kotlinTypeVariable(type)
            )
        }
        kotlinString.appendLine(kotlinDefinition)
        return kotlinString.toString()
    }

    private fun typealiasDefinition(id: String, kotlinType: String) = "typealias $id = $kotlinType"
}