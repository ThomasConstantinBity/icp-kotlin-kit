package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidServiceParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileService
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.CandidDefinitionHelper
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLServiceHelper

internal class KotlinServiceGenerator(
    private val idlFileService: IDLFileService,
    private val serviceName: String,
    private val showCandidDefinition: Boolean = true,
    private val removeCandidComment: Boolean = false
) {

    private val idlServiceDeclaration = CandidServiceParser.parseService(idlFileService.serviceDefinition)
    private val constructParams = hashMapOf(
        "canister" to "ICPPrincipal",
        "icpCanisterRepository" to "ICPCanisterRepository"
    )

    init {
        // TODO, add initArgsDeclaration
    }

    fun getKotlinServiceDefinition(): String {
        val serviceKotlinString = StringBuilder()

        // Candid definition
        if (showCandidDefinition) {
            val candidDefinition = CandidDefinitionHelper.candidDefinition(
                definition = idlFileService.serviceDefinition,
                removeCandidComment = removeCandidComment
            )
            serviceKotlinString.appendLine("""
                /**
                    $candidDefinition
                */
            """.trimIndent())
        }

        // Service comment
        idlFileService.comment?.let {
            serviceKotlinString.appendLine(KotlinCommentGenerator.getKotlinComment(it))
        }

        serviceKotlinString.appendLine(
            """
                class ${serviceName}Service ${privateConstructorDefinition()} {
                    ${serviceFunction()}
                    $PRIVATE_QUERY_DECLARATION
                    ${companionObjectDefinition()}
                }
            """.trimIndent()
        )

        return serviceKotlinString.toString()
    }

    private fun privateConstructorDefinition(): String {
        return "private constructor(${constructParams
            .map { "private val ${it.key}: ${it.value}" }
            .joinToString(separator = ",\n", prefix = "\n")}\n)"
    }

    private fun companionObjectDefinition(): String {
        val filteredConstructorParams = constructParams
            .filter { it.key != "icpCanisterRepository" }
        val initParams = filteredConstructorParams
            .map { "${it.key}: ${it.value}" }
            .joinToString(separator = ",\n", prefix = "\n")
        val initParam = filteredConstructorParams
            .map { "${it.key} = ${it.key}" }
            .joinToString(separator = ",\n", prefix = "\n")
        return """companion object {
                fun init($initParams
                ): ${serviceName}Service =
                    ${serviceName}Service ($initParam,
                        icpCanisterRepository = provideICPCanisterRepository()
                    )
                }"""
    }

    private fun serviceFunction(): String {
        val serviceKotlinString = StringBuilder()
        idlServiceDeclaration.services.forEach { service ->

            // Comment
            service.comment?.let { comment ->
                serviceKotlinString.append(KotlinCommentGenerator.getKotlinComment(comment))
            }

            serviceKotlinString.appendLine(
                IDLServiceHelper(
                    idlService = service,
                ).convertServiceIntoKotlinFunction()
            )
        }
        return serviceKotlinString.toString()
    }

    companion object {
        private const val PRIVATE_QUERY_DECLARATION =
            """private suspend fun query(
                method: ICPMethod,
                certification: ICPRequestCertification,
                sender: ICPSigningPrincipal? = null,
                pollingValues: PollingValues
            ): Result<CandidValue> =
                when(certification) {
                    ICPRequestCertification.Uncertified -> icpCanisterRepository.query(method)
                    ICPRequestCertification.Certified -> {
                        val requestId = icpCanisterRepository.call(
                            method = method,
                            sender = sender
                        ).getOrElse { return Result.failure(it) }
                        icpCanisterRepository.pollRequestStatus(
                            requestId = requestId,
                            canister = method.canister,
                            sender = sender,
                            durationSeconds = pollingValues.durationSeconds,
                            waitDurationSeconds = pollingValues.waitDurationSeconds
                        )
                    }
                }
            """
    }
}