package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.CandidServiceParamParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName

internal object IDLServiceHelper {

    fun convertServiceIntoKotlinFunction(idlService: IDLService): String {
        val functionDeclaration = StringBuilder().append("suspend fun ${idlService.id}")

        // Input args
        if(idlService.inputParamsDeclaration.isNotEmpty())
            functionDeclaration.append(
                """
                    (
                        ${inputArgsDeclaration(idlService.inputParamsDeclaration)}
                    )
                """.trimIndent()
            )
        else
            functionDeclaration.append("()")

        // Output args
        val outputKotlinDeclaration = outputArgsDeclaration(idlService.outputParamsDeclaration)
        if(outputKotlinDeclaration.isNotEmpty())
            functionDeclaration.append(": $outputKotlinDeclaration")
        functionDeclaration.appendLine(" {")

        // Function body
        functionDeclaration.appendLine("TODO()")

        functionDeclaration.append("}")
        return functionDeclaration.toString()
    }

    private fun inputArgsDeclaration(inputArgs: String): String {
        val idlServiceParam = CandidServiceParamParser.parseServiceParam(inputArgs)
        if(idlServiceParam.params.isEmpty()) return ""
        var primitiveTypeIndex = 0
        return idlServiceParam.params.joinToString(",\n") {
            val kotlinClassType = IDLTypeHelper.kotlinTypeVariable(it)
            if(it is IDLTypeCustom)
                "${kotlinClassType.kotlinVariableName()}: $kotlinClassType"
            else {
                val variableName = "_$primitiveTypeIndex: $kotlinClassType"
                primitiveTypeIndex++
                variableName
            }
        }
    }

    private fun outputArgsDeclaration(outputArgs: String): String {
        val idlServiceParam = CandidServiceParamParser.parseServiceParam(outputArgs)
        val params = idlServiceParam.params
        return when {
            params.isEmpty() -> ""
            params.size == 1 -> IDLTypeHelper.kotlinTypeVariable(params.first())
            else -> "NTuple${params.size}<${params.joinToString { IDLTypeHelper.kotlinTypeVariable(it) }}>"
        }
    }
}