package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

internal class KotlinClassParameter(
    val comment: String? = null,
    val id: String?,
    val type: IDLType,
    val kotlinClassType: KotlinClassDefinitionType?,
    val isOptional: Boolean,
) {
    private val typeVariable: String
    private val valId: String

    init {
        typeVariable = when {
            type is IDLTypeVec -> {
                val idlVec = CandidVecParser.parseVec(type.vecDeclaration)
                "Array<${kotlinClassType?.name ?: IDLTypeHelper.kotlinTypeVariable(idlVec.type)}>"
            }
            else -> IDLTypeHelper.kotlinTypeVariable(type)
        }
        valId = id ?: typeVariable.replaceFirstChar { it.lowercase() }
    }

    fun kotlinDefinition(): String {
        val kotlinDefinition = StringBuilder()
        comment?.let { kotlinDefinition.append(it) }
        val typeDeclaration = if(isOptional) "$typeVariable?" else typeVariable
        kotlinDefinition.append("val $valId: $typeDeclaration")
        return kotlinDefinition.toString()
    }
}