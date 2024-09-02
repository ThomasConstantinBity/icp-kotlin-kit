package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileType
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.CandidDefinitionHelper

internal object KotlinClassGenerator {

    fun getTypeAliasAndClassDefinitions(
        types: List<IDLFileType>,
        fileName: String,
        showCandidDefinition: Boolean,
        removeCandidComment: Boolean
    ): Pair<List<String>, List<String>> {

        val typeAliases = mutableListOf<String>()
        val classesDeclarations = mutableListOf<String>()

        types.forEach { idlFileType ->
            val definition = StringBuilder()

            if(showCandidDefinition) {
                definition.appendLine(
                    """
                        /**
                        ${CandidDefinitionHelper.candidDefinition(
                            definition = idlFileType.typeDefinition,
                            removeCandidComment = removeCandidComment
                        )}
                        */
                    """.trimIndent()
                )
            }

            // Comment
            idlFileType.comment?.let { comment ->
                definition.append(KotlinCommentGenerator.getKotlinComment(comment))
            }

            val kotlinClassDefinition = IDLTypeDeclarationConverter(
                input = idlFileType.typeDefinition,
                className = fileName
            )
            definition.appendLine(kotlinClassDefinition)
            if(kotlinClassDefinition.startsWith("typealias"))
                typeAliases.add(definition.toString())
            else classesDeclarations.add(definition.toString())
        }
        return Pair(typeAliases, classesDeclarations)
    }
}