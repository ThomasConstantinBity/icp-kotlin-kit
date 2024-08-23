package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidTypeParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class KotlinFileGenerator(
    private val idlFileDeclaration: IDLFileDeclaration
) {

    fun getFileText(): String {

        val fileText = StringBuilder()

        // File header
        fileText.append(fileHeader())
        fileText.append("\n\n")

        // File comment
        idlFileDeclaration.comment?.let {
            fileText.append(KotlinCommentGenerator.getKotlinComment(it))
            fileText.append("\n")
        }

        // Type declaration
        idlFileDeclaration.types.forEach { idlFileType ->

            // Comment
            idlFileType.comment?.let { comment ->
                fileText.append(KotlinCommentGenerator.getKotlinComment(comment))
            }

            val idlType = CandidTypeParser.parseType(idlFileType.typeDefinition)
            fileText.append(KotlinClassGenerator.kotlinClass(idlType))
            fileText.append("\n\n")
        }

        return fileText.toString()
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