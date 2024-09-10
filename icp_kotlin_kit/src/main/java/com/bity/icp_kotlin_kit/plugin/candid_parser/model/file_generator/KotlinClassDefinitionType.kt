package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

// TODO remove =  null
internal sealed class KotlinClassDefinitionType(
    val name: String,
    val innerClasses: MutableList<KotlinClassDefinitionType> = mutableListOf()
) {

    class TypeAlias(
        val typeAliasId: String,
        val className: String?,
        val type: IDLType
    ): KotlinClassDefinitionType(typeAliasId) {
        override fun kotlinDefinition(): String =
            "typealias $typeAliasId = ${IDLTypeHelper.kotlinTypeVariable(type, className)}"
    }

    class Function(
        val functionName: String,
        val inputArgs: List<KotlinClassDefinitionType>,
        val outputArgs: List<KotlinClassDefinitionType>
    ): KotlinClassDefinitionType(functionName) {

        override fun kotlinDefinition(): String {
            val functionResult = when(val size = outputArgs.size) {
                0 -> "Unit"
                1 -> outputArgs.first().name
                else -> TODO("Function must return multiple args, use NTuple$size")
            }
            return """
                class $functionName(
                        methodName: String,
                        canister: ICPPrincipal
                ) : ICPQuery (
                    methodName = methodName,
                    canister = canister
                ) {
                    suspend operator fun invoke(args: List<Any>): $functionResult {
                        val result = query(args).getOrThrow()
                        return CandidDecoder.decodeNotNull(result)
                    }
                }
                """.trimIndent()

        }
    }

    class SealedClass(
        val className: String,
    ): KotlinClassDefinitionType(className) {

        var inheritedClass: KotlinClassDefinitionType? = null

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

        override fun kotlinDefinition(): String {
            return inheritedClass?.let {
                "data object $objectName : ${it.name}()"
            } ?: "object $objectName"
        }
    }

    class Class(
        val className: String,
    ): KotlinClassDefinitionType(className) {

        var params: MutableList<KotlinClassParameter> = mutableListOf()
        var inheritedClass: KotlinClassDefinitionType? = null

        override fun kotlinDefinition(): String {
            val kotlinDefinition = StringBuilder("data class $className (")
            kotlinDefinition.appendLine(
                params.joinToString(
                    separator = ",\n",
                    prefix = "\n"
                ) { it.kotlinDefinition() }
            )

            val closingLine = inheritedClass?.let {
                ") : ${it.name}()"
            } ?: ")"
            kotlinDefinition.append(closingLine)

            if(innerClasses.isNotEmpty()) {
                kotlinDefinition.appendLine(" {")
                // Add inner classes
                innerClasses.filter { it !is TypeAlias }
                    .forEach {
                        kotlinDefinition.appendLine(it.kotlinDefinition())
                    }
                kotlinDefinition.appendLine("}")
            }

            return kotlinDefinition.toString()
        }

    }

    abstract fun kotlinDefinition(): String
}