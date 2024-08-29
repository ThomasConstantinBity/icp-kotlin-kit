package com.bity.icp_kotlin_kit.plugin.file_generator.helper

internal object CandidDefinitionHelper {
    fun candidDefinition(definition: String, removeCandidComment: Boolean) =
        definition.lines()
            .filter {
                if(removeCandidComment) !it.trim().startsWith("//")
                else true
            }
            .joinToString("\n") { "* $it" }
}