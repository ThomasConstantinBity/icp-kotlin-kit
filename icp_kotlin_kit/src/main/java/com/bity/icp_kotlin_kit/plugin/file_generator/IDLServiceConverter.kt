package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinitionType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
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
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.CandidServiceParamParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

internal class IDLServiceConverter(
    private val idlService: IDLService,
    private val generatedClasses: HashMap<String, KotlinClassDefinitionType>
) {

    private val inputs: HashMap<String, String> = hashMapOf()
    private val outputs = CandidServiceParamParser.parseServiceParam(idlService.outputParamsDeclaration)

    init {
        populateInputsMap()
    }

    private fun populateInputsMap() {
        CandidServiceParamParser
            .parseServiceParam(idlService.inputParamsDeclaration)
            .params
            .forEach {
                val variableType = IDLTypeHelper.kotlinTypeVariable(it)
                val variableName = when(it) {
                    is IDLFun -> TODO()
                    is IDLTypeBlob -> TODO()
                    is IDLTypeBoolean -> TODO()
                    is IDLTypeCustom -> it.typeDef.kotlinVariableName()
                    is IDLTypeFuncDeclaration -> TODO()
                    is IDLTypeInt -> TODO()
                    is IDLTypeNat -> TODO()
                    is IDLTypeNat64 -> TODO()
                    is IDLTypeNull -> TODO()
                    is IDLTypePrincipal -> TODO()
                    is IDLTypeRecord -> TODO()
                    is IDLTypeText -> TODO()
                    is IDLTypeVariant -> TODO()
                    is IDLTypeVec -> TODO()
                }
                inputs[variableName] = variableType
            }
    }

    fun getKotlinFunction(): String {
        val kotlinFunction = StringBuilder()

        // Comment
        idlService.comment?.let {
            kotlinFunction.append(KotlinCommentGenerator.getKotlinComment(it))
        }

        kotlinFunction.append("suspend fun ${idlService.id}(")

        val inputsArgs = if(inputs.entries.isNotEmpty()) {
            inputs.entries.joinToString(
                prefix = "\n",
                separator = ",\n",
                postfix = "\n"
            ) {
                "${it.key}: ${it.value}"
            }
        } else ""
        kotlinFunction.append("${inputsArgs}): ")

        when(val outputParamSize = outputs.params.size) {
            0 -> kotlinFunction.appendLine("Unit {")
            1 -> kotlinFunction.appendLine("${IDLTypeHelper.kotlinTypeVariable(outputs.params.first())} {")
            else -> TODO()
        }

        // Function Body
        kotlinFunction.appendLine(
            """
                val result = query(
                    args = ${getQueryArgsDeclaration()}
                ).getOrThrow()
                return CandidDecoder.decodeNotNull(result)
            """.trimIndent()
        )

        kotlinFunction.appendLine("}")
        return kotlinFunction.toString()
    }

    private fun getQueryArgsDeclaration(): String {
        require(inputs.entries.isNotEmpty()) {
            return "null"
        }
        return "listOf(${inputs.entries.joinToString { it.key }})"
    }
}