package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidServiceParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileService
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.CandidDefinitionHelper
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLServiceHelper

internal object KotlinServiceGenerator {

    // TODO, add service name
    fun getServiceText(
        idlFileService: IDLFileService,
        showCandidDefinition: Boolean = true,
        removeCandidComment: Boolean = false
    ): String {
        val serviceKotlinString = StringBuilder()

        // Candid definition
        if(showCandidDefinition) {
            serviceKotlinString.appendLine(
                """
                    /**
                     ${CandidDefinitionHelper
                         .candidDefinition(
                             definition = idlFileService.serviceDefinition, 
                             removeCandidComment = removeCandidComment
                         )}
                     */
                    """.trimIndent()
            )
        }

        // Service comment
        idlFileService.comment?.let {
            val comment = KotlinCommentGenerator.getKotlinComment(it)
            serviceKotlinString.appendLine(comment)
        }

        val idlServiceDeclaration = CandidServiceParser.parseService(idlFileService.serviceDefinition)

        // TODO, add initArgsDeclaration
        val constructorParams = StringBuilder(
            "private val icpCanisterRepository: ICPCanisterRepository"
        )
        val additionalConstructorParams: String? = idlServiceDeclaration.initArgsDeclaration?.let {
            TODO("Convert and add param")
        }

        serviceKotlinString.appendLine(
            """
                class Service private constructor(
                    $constructorParams
                ){
            """.trimIndent()
        )

        idlServiceDeclaration.services.forEach { service ->
            service.comment?.let { comment ->
                serviceKotlinString.append(KotlinCommentGenerator.getKotlinComment(comment))
            }
            serviceKotlinString.appendLine(
                IDLServiceHelper.convertServiceIntoKotlinFunction(service)
            )
        }

        // Companion object
        serviceKotlinString.appendLine("""
            ${companionObjectDefinition(
                // TODO
                serviceClassName = "Service",
                additionalConstructorParams = additionalConstructorParams
            )}
        """.trimIndent())

        serviceKotlinString.appendLine("}")
        return serviceKotlinString.toString()
    }

    private fun companionObjectDefinition(
        serviceClassName: String,
        additionalConstructorParams: String?
    ): String {
        return if(additionalConstructorParams != null)
            TODO()
         else """
            companion object {
                fun init(): $serviceClassName =
                    $serviceClassName(
                        icpCanisterRepository = provideICPCanisterRepository()
                    )
            }
        """.trimIndent()
    }
}