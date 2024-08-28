package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.toKotlinFileString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object KotlinFileGenerator {

    fun getFileText(
        idlFileDeclaration: IDLFileDeclaration,
        showCandidDefinition: Boolean = true,
        removeCandidComment: Boolean = false
    ): String {

        val kotlinClasses = StringBuilder()

        // Type declaration
        idlFileDeclaration.types.forEach { idlFileType ->

            if(showCandidDefinition) {
                val candidDefinition = idlFileType.typeDefinition
                    .lines()
                    .filter {
                        if(removeCandidComment) !it.trim().startsWith("//")
                        else true
                    }
                    .joinToString("\n") { "* $it" }
                kotlinClasses.appendLine(
                    """
                    /**
                     $candidDefinition
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

        val packageAndImports = StringBuilder().appendLine(
            """// TODO, add package name
                
               // TODO add imports
            """.trimMargin())

        idlFileDeclaration.comment?.let {
            packageAndImports.append(KotlinCommentGenerator.getKotlinComment(it))
        }

        return """$packageAndImports
            ${fileHeader().trimIndent()}
            
            $kotlinClasses
        """.toKotlinFileString()
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