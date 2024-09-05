package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

internal class KotlinClassParameter(
    val comment: String? = null,
    val id: String?,
    val type: IDLType,
    val isOptional: Boolean,
    val className: String
) {

    private val typeVariable = IDLTypeHelper.kotlinTypeVariable(type, className)
    private val valId = id ?: typeVariable.replaceFirstChar { it.lowercase() }

    fun kotlinDefinition(): String {
        val kotlinDefinition = StringBuilder()

        comment?.let { kotlinDefinition.appendLine(it) }
        val typeDeclaration = if(isOptional) "$typeVariable?" else typeVariable
        kotlinDefinition.append("val $valId: $typeDeclaration")
        return kotlinDefinition.toString()
    }

    fun kotlinVariableConstructor(): String {
        val candidDecoderFunction = if(isOptional) "decode" else "decodeNotNull"
        val funParam = if(isOptional) "candidRecord.dictionary[\"$valId\"]" else
            "candidRecord.dictionary.getNotNull(\"$valId\")"
        return "$valId = CandidDecoder.$candidDecoderFunction($funParam)"
    }
}