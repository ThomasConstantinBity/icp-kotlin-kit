package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.enum.KotlinClassDefinitionType

internal data class KotlinClassDefinition(
    val definitionName: String,
    val candidDefinition: String,
    val kotlinDefinition: String,
    val classDefinitionType: KotlinClassDefinitionType
)