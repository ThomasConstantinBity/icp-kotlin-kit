package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileType
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.toKotlinFileString
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.CandidDefinitionHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object KotlinFileGenerator {

    fun getFileText(
        idlFileDeclaration: IDLFileDeclaration,
        showCandidDefinition: Boolean = true,
        removeCandidComment: Boolean = false
    ): String {

        val packageAndImports = StringBuilder().appendLine(
            """// TODO, add package name
                
               import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
               import com.bity.icp_kotlin_kit.plugin.candid_parser.util.shared.*
               
               ${fileHeader()}
            """.trimMargin())
        idlFileDeclaration.comment?.let {
            packageAndImports.appendLine()
            packageAndImports.append(KotlinCommentGenerator.getKotlinComment(it))
        }

        val kotlinClasses = convertIDLFileTypeToKotlinClasses(
            types = idlFileDeclaration.types,
            showCandidDefinition = showCandidDefinition,
            removeCandidComment = removeCandidComment
        )

        val kotlinService = idlFileDeclaration.service?.let {
            KotlinServiceGenerator.getServiceText(
                idlFileService = it,
                showCandidDefinition = showCandidDefinition,
                removeCandidComment = removeCandidComment
            )
        } ?: ""

        return """
            $packageAndImports
            $kotlinClasses
            $kotlinService
        """.toKotlinFileString()
    }

    private fun convertIDLFileTypeToKotlinClasses(
        types: List<IDLFileType>,
        showCandidDefinition: Boolean,
        removeCandidComment: Boolean
    ): String {
        val kotlinClasses = StringBuilder()
        types.forEach { idlFileType ->

            if(showCandidDefinition) {
                kotlinClasses.appendLine(
                    """
                        /**
                         ${CandidDefinitionHelper
                             .candidDefinition(
                                 definition = idlFileType.typeDefinition, 
                                 removeCandidComment = removeCandidComment
                             )}
                         */
                    """.trimIndent()
                )
            }

            // Comment
            idlFileType.comment?.let { comment ->
                kotlinClasses.append(KotlinCommentGenerator.getKotlinComment(comment))
            }

            val kotlinClassDefinition = IDLTypeDeclarationConverter(idlFileType.typeDefinition)
            kotlinClasses.appendLine(kotlinClassDefinition)
        }
        return kotlinClasses.toString()
    }

    private fun fileHeader(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault())
        val currentDate = sdf.format(Date())
        return """
            /**
             * File generated at $currentDate using ICP Kotlin Kit Plugin
             */
        """.trimIndent()
    }
}