package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVariantParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
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

            val kotlinClassDefinition = when(val type = it.type) {
                is IDLTypeRecord -> IDLTypeRecordHelper.typeRecordToKotlinClass(
                    className = it.id,
                    type = type
                )
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
                is IDLTypeCustom -> IDLTypeCustomHelper.idlTypeCustomToKotlinClass(
                    className = it.id,
                    idlTypeCustom = type
                )
                else -> IDLTypeHelper.kotlinTypeVariable(type)
            }

            kotlinClassString.appendLine("$kotlinClassDefinition : ${className}()")
        }

        kotlinClassString.append("}")
        return kotlinClassString.toString()
    }

}