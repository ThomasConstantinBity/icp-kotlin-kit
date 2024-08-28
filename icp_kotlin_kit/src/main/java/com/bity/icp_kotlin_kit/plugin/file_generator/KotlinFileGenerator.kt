package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.toKotlinFileString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object KotlinFileGenerator {

    fun getFileText(idlFileDeclaration: IDLFileDeclaration): String {

        val kotlinClasses = StringBuilder()
        val imports = mutableSetOf<String>()

        // Type declaration
        idlFileDeclaration.types.forEach { idlFileType ->

            val candidDefinition = idlFileType.typeDefinition
                .lines()
                .joinToString("\n") { "* $it" }
            kotlinClasses.appendLine(
                """
                    /**
                     $candidDefinition
                     */
                """.trimIndent()
            )

            // Comment
            idlFileType.comment?.let { comment ->
                kotlinClasses.append(KotlinCommentGenerator.getKotlinComment(comment))
            }

            val kotlinClassDefinition = KotlinClassGenerator.kotlinClassDefinition(idlFileType.typeDefinition)
            kotlinClasses.appendLine(kotlinClassDefinition.kotlinClassString)
            imports.addAll(kotlinClassDefinition.import)
        }

        val packageAndImports = StringBuilder().appendLine("// TODO, add package name\n")
        if(imports.isNotEmpty()) {
            packageAndImports.appendLine(imports.joinToString("\n"))
        }
        idlFileDeclaration.comment?.let {
            packageAndImports.appendLine(KotlinCommentGenerator.getKotlinComment(it))
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