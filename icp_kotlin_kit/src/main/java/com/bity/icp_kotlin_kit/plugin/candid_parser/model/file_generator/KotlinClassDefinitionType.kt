package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

// TODO remove =  null
internal sealed class KotlinClassDefinitionType(
    val name: String
    // val parent: KotlinClassDefinitionType? = null
) {

    class TypeAlias(
        val typeAliasId: String,
        val className: String?,
        val type: IDLType
    ): KotlinClassDefinitionType(typeAliasId) {
        override fun kotlinDefinition(): String =
            "typealias $typeAliasId = ${IDLTypeHelper.kotlinTypeVariable(type, className)}"
    }
    // Function,
    // TypeAlias,
    // Array,
    class SealedClass(
        val className: String,
    ): KotlinClassDefinitionType(className) {

        var inheritedClass: KotlinClassDefinitionType? = null
        val innerClasses = mutableListOf<KotlinClassDefinitionType>()

        override fun kotlinDefinition(): String {
            return """
                sealed class $className {
                    ${innerClasses.joinToString("\n") { it.kotlinDefinition() }}
                }
            """.trimIndent()
        }
    }

    class Object(
        val objectName: String,
        // parent: KotlinClassDefinitionType?
    ): KotlinClassDefinitionType(objectName) {

        var inheritedClass: KotlinClassDefinitionType? = null
        val innerClasses = mutableListOf<KotlinClassDefinitionType>()

    }

    class Class(
        val className: String,
        val params: List<KotlinClassParameter>,
    ): KotlinClassDefinitionType(className) {

        var inheritedClass: KotlinClassDefinitionType? = null

        override fun kotlinDefinition(): String {
            val kotlinDefinition = StringBuilder("data class $className(")
            kotlinDefinition.appendLine(
                params.joinToString(
                    separator = ",\n",
                    prefix = "\n"
                ) { it.kotlinDefinition() }
            )

            val closingLine = inheritedClass?.let {
                ") : ${it.name}() {\n"
            } ?: ") {\n"
            kotlinDefinition.appendLine(closingLine)


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