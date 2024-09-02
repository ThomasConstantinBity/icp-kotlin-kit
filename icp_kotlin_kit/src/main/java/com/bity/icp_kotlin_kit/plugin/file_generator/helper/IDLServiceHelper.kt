package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.CandidServiceParamParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName
import org.gradle.configurationcache.extensions.capitalized

internal class IDLServiceHelper(
    private val idlService: IDLService
) {

    private val baseFunctionParam = hashMapOf(
        "sender" to "ICPSigningPrincipal? = null",
        "certification" to "ICPRequestCertification = ${defaultCertificationForFunction(idlService.serviceType)}",
        "pollingValues" to "PollingValues = PollingValues()"
    )

    private val additionalFunctionParam = hashMapOf<String, IDLType>()
    private val resultParams = CandidServiceParamParser
        .parseServiceParam(idlService.outputParamsDeclaration)
        .params
    private val kotlinFunctionResultClasses = mutableListOf<String>()

    init {
        var primitiveTypeIndex = 0
        val idlServiceParam = CandidServiceParamParser
            .parseServiceParam(idlService.inputParamsDeclaration)

        // Input params
        idlServiceParam.params.forEach {
            val kotlinClassType = IDLTypeHelper.kotlinTypeVariable(it)
            if(it is IDLTypeCustom)
                additionalFunctionParam[kotlinClassType.kotlinVariableName()] = it
            else {
                val variableName = "_unnamedVariable$primitiveTypeIndex"
                primitiveTypeIndex++
                additionalFunctionParam[variableName] = it
            }
        }

        // Check for vec record in output param
        // TODO, multiple record as result
        resultParams.filterIsInstance<IDLTypeVec>().forEach {
            val idlVec = CandidVecParser.parseVec(it.vecDeclaration)
            if(idlVec.type is IDLTypeRecord) {
                kotlinFunctionResultClasses.add(
                    IDLTypeRecordHelper.typeRecordToKotlinClass(
                        className = "${idlService.id.toClassName()}Response",
                        type = idlVec.type)
                )
            }
        }
    }

    private fun inputArgs(): String =
        (baseFunctionParam.map { "${it.key}: ${it.value}" } + additionalFunctionParam.map { "${it.key}: ${IDLTypeHelper.kotlinTypeVariable(it.value)}" })
            .joinToString(",\n")

    fun convertServiceIntoKotlinFunction(): String {

        val functionDeclaration = StringBuilder().append("suspend fun ${idlService.id}")

        // Input args
        functionDeclaration.append("(\n${inputArgs()}\n)")

        // Output args
        val outputKotlinDeclaration = outputArgsDeclaration()
        if(outputKotlinDeclaration.isNotEmpty())
            functionDeclaration.append(": $outputKotlinDeclaration")
        functionDeclaration.appendLine(" {")

        // Function body
        functionDeclaration.appendLine(serviceFunctionBody(idlService.id,))

        functionDeclaration.appendLine("}")
        functionDeclaration.appendLine(
            kotlinFunctionResultClasses.joinToString(
                prefix = "\n",
                separator = "\n"
            )
        )
        return functionDeclaration.toString()
    }

    private fun outputArgsDeclaration(): String =
        when {
            resultParams.isEmpty() -> ""
            resultParams.size == 1 ->
                IDLTypeHelper.kotlinTypeVariable(
                    type = resultParams.first(),
                    className = "${idlService.id.toClassName()}Response"
                )
            else -> "NTuple${resultParams.size}<${resultParams.joinToString { IDLTypeHelper.kotlinTypeVariable(it) }}>"
        }
    
    private fun defaultCertificationForFunction(idlServiceType: IDLServiceType?) =
        when(idlServiceType) {
            null,
            IDLServiceType.Query -> "ICPRequestCertification.Uncertified"
            IDLServiceType.OneWay -> TODO()
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
        """.trimIndent()

        return """
            $icpMethodDeclaration
            $functionCall
            ${returnTypeForFunction()}
        """.trimIndent()
    }

    private fun returnTypeForFunction(): String {
        return if(resultParams.find { it.isOptional } != null)
            "return CandidDecoder.decodeNullable(result)"
        else "return CandidDecoder.decode(result)"
    }

    private fun String.toClassName() =
        this.split("_")
            .joinToString("") { it.capitalized() }
}