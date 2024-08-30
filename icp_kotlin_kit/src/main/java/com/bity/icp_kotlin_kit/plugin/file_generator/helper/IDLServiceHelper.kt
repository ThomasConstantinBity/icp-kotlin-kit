package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.CandidServiceParamParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName

internal object IDLServiceHelper {

    fun convertServiceIntoKotlinFunction(idlService: IDLService): String {
        val functionDeclaration = StringBuilder().append("suspend fun ${idlService.id}")

        // Input args
        functionDeclaration.append("(\n${inputArgsDeclaration(idlService.inputParamsDeclaration, idlService.serviceType)}\n)")

        // Output args
        val outputKotlinDeclaration = outputArgsDeclaration(idlService.outputParamsDeclaration)
        if(outputKotlinDeclaration.isNotEmpty())
            functionDeclaration.append(": $outputKotlinDeclaration")
        functionDeclaration.appendLine(" {")

        // Function body
        functionDeclaration.appendLine(
            serviceFunctionBody(
                methodName = idlService.id,
                inputArgs = idlService.inputParamsDeclaration
            )
        )

        functionDeclaration.append("}")
        return functionDeclaration.toString()
    }

    private fun inputArgsDeclaration(
        inputArgs: String,
        idlServiceType: IDLServiceType?
    ): String {
        val idlServiceParam = CandidServiceParamParser.parseServiceParam(inputArgs)
        val baseArgsDeclaration = """
                sender: ICPSigningPrincipal? = null,
                certification: ICPRequestCertification = ${defaultCertificationForFunction(idlServiceType)},
                pollingValues: PollingValues = PollingValues()
            """.trimIndent()
        if(idlServiceParam.params.isEmpty()) 
            return baseArgsDeclaration
        var primitiveTypeIndex = 0
        val functionInputArgs = idlServiceParam.params.joinToString(",\n") {
            val kotlinClassType = IDLTypeHelper.kotlinTypeVariable(it)
            if(it is IDLTypeCustom)
                "${kotlinClassType.kotlinVariableName()}: $kotlinClassType".trim()
            else {
                val variableName = "_$primitiveTypeIndex: $kotlinClassType"
                primitiveTypeIndex++
                variableName
            }
        }
        return """
            $functionInputArgs,
            $baseArgsDeclaration
        """.trimIndent()
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

    private fun serviceFunctionBody(
        methodName: String,
        inputArgs: String
    ): String {
        val icpMethodArgs =
            if(inputArgs.isNotEmpty()) "CandidEncoder(${inputArgs.kotlinVariableName()})"
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