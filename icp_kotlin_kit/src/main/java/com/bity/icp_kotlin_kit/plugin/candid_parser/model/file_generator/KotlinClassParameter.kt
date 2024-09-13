package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.file_generator.KotlinCommentGenerator

internal data class KotlinClassParameter(
    private val comment: IDLComment? = null,
    private val id: String,
    val isOptional: Boolean,
    private val typeVariable: String
) {

    private val typeDeclaration = if (isOptional) "$typeVariable?" else typeVariable

    fun constructorDefinition(): String {
        val kotlinDefinition = StringBuilder()
        comment?.let {
            kotlinDefinition.append(
                KotlinCommentGenerator.getKotlinComment(it)
            )
        }
        kotlinDefinition.append("val $id: $typeDeclaration")
        return kotlinDefinition.toString()
    }
}