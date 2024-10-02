package com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.FunType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.file_generator.KotlinCommentGenerator
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

internal sealed class KotlinClassDefinition(
    val name: String
) {

    var inheritedClass: KotlinClassDefinition? = null
    val innerClasses: MutableList<KotlinClassDefinition> = mutableListOf()

    class TypeAlias(
        val typeAliasId: String,
        val type: IDLType,
        val typeClassName: String?
    ): KotlinClassDefinition(typeAliasId) {
        override fun kotlinDefinition(): String =
            "typealias $typeAliasId = ${IDLTypeHelper.kotlinTypeVariable(type, typeClassName)}"
    }

    class Function(
        private val functionName: String,
        private val outputArgs: List<KotlinClassParameter>,
    ): KotlinClassDefinition(functionName) {

        override fun kotlinDefinition(): String {
            val functionResult = when(val size = outputArgs.size) {
                0 -> "Unit"
                1 -> outputArgs.first().typeDeclaration
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
                    suspend operator fun invoke(
                        args: List<Any>,
                        certification: ICPRequestCertification = ICPRequestCertification.Uncertified,
			            sender: ICPSigningPrincipal? = null,
			            pollingValues: PollingValues = PollingValues()
                    ): $functionResult {
                        val result = query(
                            args = args,
                            certification = certification,
				            sender = sender,
				            pollingValues = pollingValues
                        ).getOrThrow()
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
    }

    class Object(
        val objectName: String,
    ): KotlinClassDefinition(objectName) {

        override fun kotlinDefinition(): String {
            return inheritedClass?.let {
                "data object $objectName : ${it.name}()"
            } ?: "object $objectName"
        }
    }

    data class Class(
        val className: String,
    ): KotlinClassDefinition(className) {

        var params: MutableList<KotlinClassParameter> = mutableListOf()

        override fun kotlinDefinition(): String {
            if(className == "TransferArgs")
                println()
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
    }

    class ICPQuery(
        private val comment: IDLComment? = null,
        private val queryName: String,
        private val funType: FunType?
    ) : KotlinClassDefinition(queryName) {

        val inputArgs = mutableListOf<KotlinClassParameter>()
        val outputArgs = mutableListOf<KotlinClassParameter>()

        override fun kotlinDefinition(): String {
            val returnParam = when (outputArgs.size) {
                0 -> ""
                1 -> ": ${outputArgs.first().typeDeclaration}"
                else -> TODO()
            }
            val kotlinComment = KotlinCommentGenerator.getNullableKotlinComment(comment) ?: ""
            return """
            $kotlinComment
            suspend fun ${queryName}${inputArgsDefinition()}$returnParam {
                val icpQuery = ICPQuery(
                    methodName = "$queryName",
                    canister = canister
                )
                ${callQueryFun()}
                ${returnStatement()}
            }
            ${
                innerClasses.joinToString(
                    separator = "\n",
                    prefix = "\n"
                ) { it.kotlinDefinition() }
            }
        """.trimIndent()
        }

        private fun inputArgsDefinition(): String {
            val input = if(inputArgs.isNotEmpty())
            inputArgs.joinToString(
                separator = ",\n",
                prefix = "(\n",
                postfix = ","
            ) { it.functionInputArgument() }
            else "("
            return when(funType) {
                FunType.Query -> """
                    $input
                    certification: ICPRequestCertification = ICPRequestCertification.Uncertified,
			        sender: ICPSigningPrincipal? = null,
			        pollingValues: PollingValues = PollingValues()
                )
                """.trimIndent()
                null -> """
                    $input
			        sender: ICPSigningPrincipal? = null,
			        pollingValues: PollingValues = PollingValues()
                )
                """.trimIndent()
            }
        }

        private fun callQueryFun(): String {
            val argsList = inputArgs.joinToString(", ") { it.id }
            return when(funType) {
                FunType.Query ->
                    """
                        val result = icpQuery(
                            args = listOf($argsList),
                            sender = sender,
                            pollingValues = pollingValues,
                            certification = certification
                        ).getOrThrow()
                    """.trimIndent()
                null ->
                    """
                        val result = icpQuery(
                            args = listOf($argsList),
                            sender = sender,
                            pollingValues = pollingValues,
                            certification = ICPRequestCertification.Certified
                        ).getOrThrow()
                    """.trimIndent()
            }
        }

        private fun returnStatement(): String {
            return when (outputArgs.size) {
                0 -> ""
                1 -> if (outputArgs.first().isOptional)
                    "return CandidDecoder.decode(result)" else
                        "return CandidDecoder.decodeNotNull(result)"
                else -> TODO()
            }
        }
    }

    abstract fun kotlinDefinition(): String
}