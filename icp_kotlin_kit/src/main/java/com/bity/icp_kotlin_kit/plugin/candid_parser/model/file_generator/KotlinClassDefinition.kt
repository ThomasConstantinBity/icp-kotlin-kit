package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

internal sealed class KotlinClassDefinition(
    val name: String
) {

    var inheritedClass: KotlinClassDefinition? = null
    val innerClasses: MutableList<KotlinClassDefinition> = mutableListOf()

    class TypeAlias(
        val typeAliasId: String,
        val type: IDLType
    ): KotlinClassDefinition(typeAliasId) {
        override fun kotlinDefinition(): String =
            "typealias $typeAliasId = ${IDLTypeHelper.kotlinTypeVariable(type)}"
    }

    class Function(
        val functionName: String,
        val inputArgs: List<KotlinClassDefinition>,
        val outputArgs: List<KotlinClassDefinition>
    ): KotlinClassDefinition(functionName) {

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

    data class SealedClass(
        val className: String,
    ): KotlinClassDefinition(className) {

        override fun kotlinDefinition(): String {
            return """
                sealed class $className {
                    ${innerClasses.joinToString("\n") { it.kotlinDefinition() }}
                }
            """.trimIndent()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as SealedClass

            return className == other.className
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + className.hashCode()
            return result
        }


    }

    class Object(
        val objectName: String,
        // parent: KotlinClassDefinition?
    ): KotlinClassDefinition(objectName) {
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
    ): KotlinClassDefinition(arrayName) {
        override fun kotlinDefinition(): String {
            TODO()
            /*return when {
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
            }*/
        }
    }

    data class Class(
        val className: String,
    ): KotlinClassDefinition(className) {

        var params: MutableList<KotlinClassParameter> = mutableListOf()

        override fun kotlinDefinition(): String {
            val constructor = params.joinToString(
                prefix = "(\n",
                separator = ",\n",
                postfix = "\n)"
            ) { it.constructorDefinition() }
            val inheritedClassDefinition = inheritedClass?.let { ": ${it.name}()" } ?: ""
            val innerClassesDefinition = if(innerClasses.isNotEmpty()) {
                innerClasses.joinToString(
                    prefix = " {\n",
                    separator = "\n",
                    postfix = "\n}\n"
                ) { it.kotlinDefinition() }
            } else ""
            return "class ${className}${constructor}${inheritedClassDefinition}$innerClassesDefinition"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as Class

            if (className != other.className) return false
            if (params != other.params) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + className.hashCode()
            result = 31 * result + params.hashCode()
            return result
        }


        class ICPQuery(
            private val comment: IDLComment? = null,
            private val queryName: String,
            private val inputParamsDeclaration: String?,
            private val outputParamsDeclaration: String?
        ) : KotlinClassDefinition(queryName) {

            val inputArgs = mutableListOf<KotlinClassParameter>()
            val outputArgs = mutableListOf<KotlinClassParameter>()

            override fun kotlinDefinition(): String {
                /*val inputArgsDefinition = inputArgs.joinToString(", ") { it.functionInputArgument() }
            val returnParam = when(val size = outputArgs.size) {
                0 -> ""
                1 -> ": ${outputArgs.first().typeDeclaration}"
                else -> TODO()
            }
            // TODO, comment
            return """
                // ${candidDeclaration()}
                suspend fun ${queryName.kotlinFunctionName()}($inputArgsDefinition)$returnParam {
                    val icpQuery = ICPQuery(
                        methodName = "$queryName",
                        canister = canister
                    )
                    val result = icpQuery.query(listOf()).getOrThrow()
                    return CandidDecoder.${functionReturnDeclaration()}
                }
                ${innerClasses.joinToString(
                    separator = "\n",
                    prefix = "\n"
                ) { it.kotlinDefinition() }}
            """.trimIndent()*/
                TODO()
            }

            private fun candidDeclaration(): String =
                "$queryName : ${inputParamsDeclaration ?: ""} -> (${outputParamsDeclaration ?: ""}) query"

            private fun functionReturnDeclaration(): String {
                return when (val size = outputArgs.size) {
                    0 -> TODO()
                    1 -> if (outputArgs.first().isOptional) "decode(result)" else "decodeNotNull(result)"
                    else -> TODO()
                }
            }
        }
    }

    abstract fun kotlinDefinition(): String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KotlinClassDefinition

        if (inheritedClass != other.inheritedClass) return false
        if (innerClasses != other.innerClasses) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inheritedClass?.hashCode() ?: 0
        result = 31 * result + innerClasses.hashCode()
        return result
    }
}