package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

internal sealed class KotlinClassDefinitionType {
    // Function,
    // TypeAlias,
    // Array,
    // SealedClass,
    // Class,
    class Class(
        val className: String,
        val params: List<KotlinClassParameter>
    ): KotlinClassDefinitionType() {
        override fun kotlinDefinition(): String {
            val kotlinDefinition = StringBuilder("data class $className(")
            kotlinDefinition.appendLine(
                params.joinToString(
                    separator = ",\n",
                    prefix = "\n"
                ) { it.kotlinDefinition() }
            )
            kotlinDefinition.appendLine(") {\n")

            // add internal constructor
            kotlinDefinition.appendLine(
                "internal constructor(candidRecord: CandidValue.Record): this("
            )
            kotlinDefinition.appendLine(
                params.joinToString(",\n") { it.kotlinVariableConstructor() }
            )
            kotlinDefinition.appendLine(")")

            kotlinDefinition.appendLine("}")
            return kotlinDefinition.toString()
        }
    }

    open fun kotlinDefinition(): String = TODO()
}