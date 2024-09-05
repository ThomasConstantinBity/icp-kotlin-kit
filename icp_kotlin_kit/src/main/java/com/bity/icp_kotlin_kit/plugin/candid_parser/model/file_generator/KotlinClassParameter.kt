package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

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
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper
import java.lang.RuntimeException

internal class KotlinClassParameter(
    val comment: String? = null,
    val id: String?,
    val type: IDLType,
    val kotlinClassType: KotlinClassDefinitionType?,
    val isOptional: Boolean,
    parentClassName: String? = null
) {

    private val typeVariable = IDLTypeHelper.kotlinTypeVariable(type, parentClassName)
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
        return when(type) {
            is IDLFun -> TODO()
            is IDLTypeCustom -> {
                when(kotlinClassType) {
                    is KotlinClassDefinitionType.Class -> "$valId = ${kotlinClassType.name}($funParam as CandidValue.Record)"
                    is KotlinClassDefinitionType.Object -> "$valId = ${kotlinClassType.name}"
                    is KotlinClassDefinitionType.SealedClass -> "$valId = TODO()"
                    is KotlinClassDefinitionType.TypeAlias -> "$valId = CandidDecoder.$candidDecoderFunction($funParam)"
                    null -> "$valId = TODO()"
                }

            }
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> TODO()

            is IDLTypeNat,
            is IDLTypeNat64,
            is IDLTypeText,
            is IDLTypeBlob,
            is IDLTypeBoolean,
            is IDLTypeInt -> "$valId = CandidDecoder.$candidDecoderFunction($funParam)"
        }
    }
}