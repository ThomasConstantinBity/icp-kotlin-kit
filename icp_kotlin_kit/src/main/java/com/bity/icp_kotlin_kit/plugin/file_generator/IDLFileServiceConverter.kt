package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidServiceParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinitionType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileService

internal class IDLFileServiceConverter(
    private val fileName: String,
    private val idlFileService: IDLFileService,
) {

    fun getKotlinServiceDefinition(): String {
        val kotlinDefinition = StringBuilder()

        // Comment
        idlFileService.comment?.let {
            kotlinDefinition.appendLine(
                KotlinCommentGenerator.getKotlinComment(it)
            )
        }

        val serviceDeclaration = CandidServiceParser.parseService( idlFileService.serviceDefinition)

        if(serviceDeclaration.initArgsDeclaration == null) {
            kotlinDefinition.appendLine(
                """
                class ${fileName}Service(
                    private val canister: ICPPrincipal
                ) {
            """.trimIndent()
            )
        } else {
            // Need to declare additional construct params
            TODO()
        }

        val kotlinFunctions = serviceDeclaration.services
            .joinToString("\n") { IDLServiceConverter(it).getKotlinFunction() }
        kotlinDefinition.appendLine(kotlinFunctions)

        kotlinDefinition.appendLine("}")
        return kotlinDefinition.toString()
    }
}