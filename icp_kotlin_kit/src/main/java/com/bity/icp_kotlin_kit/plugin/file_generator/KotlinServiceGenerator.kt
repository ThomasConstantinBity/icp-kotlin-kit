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

        serviceKotlinString.appendLine(
            """
                class Service(){
            """.trimIndent()
        )

        idlServiceDeclaration.services.forEach { service ->
            serviceKotlinString.appendLine(
                IDLServiceHelper.convertServiceIntoKotlinFunction(service)
            )
        }

        serviceKotlinString.appendLine("}")
        return serviceKotlinString.toString()
    }
}