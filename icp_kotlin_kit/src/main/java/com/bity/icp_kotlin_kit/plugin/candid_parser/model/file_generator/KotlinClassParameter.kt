package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

internal class KotlinClassParameter(
    private val comment: String? = null,
    private val id: String,
    val isOptional: Boolean,
    typeVariable: String
) {

    val typeDeclaration = if (isOptional) "$typeVariable?" else typeVariable

    fun kotlinDefinition(): String {
        val kotlinDefinition = StringBuilder()
        comment?.let { kotlinDefinition.append(it) }
        kotlinDefinition.append("val $id: $typeDeclaration")
        return kotlinDefinition.toString()
    }

    fun functionInputArgument(): String = "$id: $typeDeclaration"
}