package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
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

internal class KotlinClassParameter(
    val comment: String? = null,
    val id: String?,
    val type: IDLType,
    val kotlinClassType: KotlinClassDefinitionType?,
    // val generatedClasses: Map<String, KotlinClassDefinitionType>,
    val isOptional: Boolean,
    parentClassName: String? = null
) {

    private val typeVariable: String
    private val valId: String

    private val candidDecoderFunction: String
    private val funParam: String

    init {
        typeVariable = when {
            type is IDLTypeVec -> {
                val idlVec = CandidVecParser.parseVec(type.vecDeclaration)
                "Array<${kotlinClassType?.name ?: IDLTypeHelper.kotlinTypeVariable(idlVec.type)}>"
            }
            else -> IDLTypeHelper.kotlinTypeVariable(type, kotlinClassType?.name)
        }
        valId = id ?: typeVariable.replaceFirstChar { it.lowercase() }
        candidDecoderFunction = if(isOptional) "decode" else "decodeNotNull"
        funParam = if(isOptional) "candidRecord.dictionary[\"$valId\"]" else
            "candidRecord.dictionary.getNotNull(\"$valId\")"
        if(valId == "blocks")
            println()
    }

    fun kotlinDefinition(): String {
        val kotlinDefinition = StringBuilder()

        comment?.let { kotlinDefinition.appendLine(it) }
        val typeDeclaration = if(isOptional) "$typeVariable?" else typeVariable
        kotlinDefinition.append("val $valId: $typeDeclaration")
        return kotlinDefinition.toString()
    }

    fun kotlinVariableConstructor(): String {
        return when(type) {
            is IDLFun -> TODO()
            is IDLTypeCustom -> typeCustomVariableConstructor()
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> """
                $valId = ICPPrincipal.init(
                    ($funParam as CandidValue.Blob).data
                )
            """.trimIndent()
            is IDLTypeRecord -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> kotlinVecConstructor()

            is IDLTypeNat,
            is IDLTypeNat64,
            is IDLTypeText,
            is IDLTypeBlob,
            is IDLTypeBoolean,
            is IDLTypeInt -> "$valId = CandidDecoder.$candidDecoderFunction($funParam)"
        }
    }

    private fun typeCustomVariableConstructor(): String {
        return when(kotlinClassType) {
            is KotlinClassDefinitionType.Class -> "$valId = ${kotlinClassType.name}($funParam as CandidValue.Record)"
            is KotlinClassDefinitionType.Object -> "$valId = ${kotlinClassType.name}"
            is KotlinClassDefinitionType.SealedClass -> {
                if(isOptional) {
                    """$valId = candidRecord.dictionary["$valId"]?.let { Transfer.init(it) }"""
                } else "$valId = ${kotlinClassType.name}.init($funParam)"
            }
            is KotlinClassDefinitionType.Function -> TODO("Function")

            // No class has been generated so far, we assume it's a simple typeAlias
            null,
            is KotlinClassDefinitionType.TypeAlias -> "$valId = CandidDecoder.$candidDecoderFunction($funParam)"
        }
    }

    // TODO, support for nested vec
    private fun kotlinVecConstructor(): String {
        require(type is IDLTypeVec)
        return kotlinClassType?.let { generatedClass ->
            return when(generatedClass) {
                is KotlinClassDefinitionType.Class -> """
                    $valId = ($funParam as CandidValue.Vector).vector.values.map { 
                        ${generatedClass.className}(it as CandidValue.Record) 
                    }.toTypedArray()
                """.trimIndent()
                is KotlinClassDefinitionType.Function -> TODO("Function")
                is KotlinClassDefinitionType.Object -> TODO("Object")
                is KotlinClassDefinitionType.SealedClass -> TODO("SealedClass")
                is KotlinClassDefinitionType.TypeAlias -> TODO("TypaAlias $id")
                // is KotlinClassDefinitionType.Array -> TODO("Array")
            }
        } ?: """TODO("Vec<$typeVariable> not found")"""
    }
}