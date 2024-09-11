package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

internal class KotlinClassParameter(
    private val comment: String? = null,
    private val id: String,
    private val isOptional: Boolean,
    private val typeVariable: String
) {

    fun kotlinDefinition(): String {
        val kotlinDefinition = StringBuilder()
        comment?.let { kotlinDefinition.append(it) }
        val typeDeclaration = if (isOptional) "$typeVariable?" else typeVariable
        kotlinDefinition.append("val $id: $typeDeclaration")
        return kotlinDefinition.toString()
    }
}