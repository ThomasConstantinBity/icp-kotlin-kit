package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinFunctionName
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

// TODO remove =  null
internal sealed class KotlinClassDefinitionType(
    val name: String,
) {
    var inheritedClass: KotlinClassDefinitionType? = null
    val innerClasses: MutableList<KotlinClassDefinitionType> = mutableListOf()

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
        override fun kotlinDefinition(): String {
            return inheritedClass?.let {
                "data object $objectName : ${it.name}()"
            } ?: "object $objectName"
        }
    }

    class Array(
        private val arrayName: String,
        private val parentClassName: String?,
        private val type: IDLType
    ): KotlinClassDefinitionType(arrayName) {
        override fun kotlinDefinition(): String {
            return when {
                inheritedClass != null && innerClasses.isNotEmpty() -> {
                    """
                        class $arrayName (
                            val ${arrayName.kotlinVariableName()}: kotlin.Array<${innerClasses.first().name}>
                        ) : ${inheritedClass!!.name}() {
                            ${innerClasses.first().kotlinDefinition()}
                        }
                    """.trimIndent()
                }

                inheritedClass != null -> {
                    """
                        class $arrayName (
                            val ${arrayName.kotlinVariableName()}: kotlin.Array<${IDLTypeHelper.kotlinTypeVariable(type)}>
                        ) : ${inheritedClass!!.name}()
                    """.trimIndent()
                }

                else -> "typealias $arrayName = Array<${IDLTypeHelper.kotlinTypeVariable(type, parentClassName)}>"
            }
        }
    }

    class Class(
        val className: String,
    ): KotlinClassDefinitionType(className) {

        var params: MutableList<KotlinClassParameter> = mutableListOf()

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

    class ICPQuery(
        private val comment: IDLComment? = null,
        private val queryName: String,
        private val inputParamsDeclaration: String?,
        private val outputParamsDeclaration: String?
    ): KotlinClassDefinitionType(queryName) {

        val inputArgs = mutableListOf<KotlinClassParameter>()
        val outputArgs = mutableListOf<KotlinClassParameter>()

        override fun kotlinDefinition(): String {
            // TODO, comment
            return """
                // ${candidDeclaration()}
                suspend fun ${queryName.kotlinFunctionName()}() {
                    val icpQuery = ICPQuery(
                        methodName = "$queryName",
                        canister = canister
                    )
                    val result = icpQuery.query(listOf()).getOrThrow()
                    return CandidDecoder.decodeNotNull(result)
                }
            """.trimIndent()
        }

        private fun candidDeclaration(): String =
            "$queryName : (${inputParamsDeclaration ?: ""}) -> (${outputParamsDeclaration ?: ""}) query"
    }

    abstract fun kotlinDefinition(): String
}