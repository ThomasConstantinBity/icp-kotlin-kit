package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.CandidServiceParamParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName

internal class IDLServiceHelper(
    private val idlService: IDLService
) {

    private val baseFunctionParam = hashMapOf(
        "sender" to "ICPSigningPrincipal? = null",
        "certification" to "ICPRequestCertification = ${defaultCertificationForFunction(idlService.serviceType)}",
        "pollingValues" to "PollingValues = PollingValues()"
    )

    private val additionalFunctionParam = hashMapOf<String, String>()

    init {
        var primitiveTypeIndex = 0
        val idlServiceParam = CandidServiceParamParser
            .parseServiceParam(idlService.inputParamsDeclaration)

        idlServiceParam.params.forEach {
            val kotlinClassType = IDLTypeHelper.kotlinTypeVariable(it)
            if(it is IDLTypeCustom)
                additionalFunctionParam[kotlinClassType.kotlinVariableName()] = kotlinClassType
            else {
                val variableName = "_unnamedVariable$primitiveTypeIndex"
                primitiveTypeIndex++
                additionalFunctionParam[variableName] = kotlinClassType
            }
        }
    }

    fun convertServiceIntoKotlinFunction(): String {

        val functionDeclaration = StringBuilder().append("suspend fun ${idlService.id}")

        // Input args
        val inputArgs = (baseFunctionParam + additionalFunctionParam)
            .map { "${it.key}: ${it.value}" }
            .joinToString(",\n")
        functionDeclaration.append("(\n${inputArgs}\n)")

        // Output args
        val outputKotlinDeclaration = outputArgsDeclaration(idlService.outputParamsDeclaration)
        if(outputKotlinDeclaration.isNotEmpty())
            functionDeclaration.append(": $outputKotlinDeclaration")
        functionDeclaration.appendLine(" {")

        // Function body
        functionDeclaration.appendLine(serviceFunctionBody(idlService.id,))

        functionDeclaration.append("}")
        return functionDeclaration.toString()
    }
    
    private fun defaultCertificationForFunction(idlServiceType: IDLServiceType?) =
        when(idlServiceType) {
            null,
            IDLServiceType.Query -> "ICPRequestCertification.Uncertified"
            IDLServiceType.OneWay -> TODO()
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

    private fun serviceFunctionBody(methodName: String, ): String {
        val icpMethodArgs = if(additionalFunctionParam.isNotEmpty())
            "CandidEncoder(${additionalFunctionParam.map { it.key }.joinToString(", ")})"
        else "null"
        val icpMethodDeclaration = """
            val icpMethod = ICPMethod(
            canister = canister,
            methodName = "$methodName",
            args = $icpMethodArgs
        )
        """.trimIndent()

        val functionCall = """
            val result = query(
                method = icpMethod,
                certification = certification,
                sender = sender,
                pollingValues = pollingValues
            ).getOrThrow()
            return CandidDecoder(result)
        """.trimIndent()

        return """
            $icpMethodDeclaration
            $functionCall
        """.trimIndent()
    }
}