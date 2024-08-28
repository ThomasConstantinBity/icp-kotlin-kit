package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class KotlinClassGeneratorTest {

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("typeBlob")
    fun `type blob` (
        input: IDLTypeDeclaration,
        expectedResult: String
    ) {
        TODO()
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("typeRecord")
    fun `type record` (
        input: IDLTypeDeclaration,
        expectedResult: String
    ) {
        TODO()
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("typeVariant")
    fun `type variant` (
        input: IDLTypeDeclaration,
        expectedResult: String
    ) {
        TODO()
    }

    companion object {

        @JvmStatic
        private fun typeBlob() = listOf(

            Arguments.of(
                IDLTypeDeclaration(
                    id = "generated_candid_file.AccountIdentifier",
                    type = IDLTypeBlob()
                ),
                """
                    typealias generated_candid_file.AccountIdentifier = ByteArray
                """.trimIndent()
            ),

            Arguments.of(
                IDLTypeDeclaration(
                    id = "generated_candid_file.AccountIdentifier",
                    isOptional = true,
                    type = IDLTypeBlob()
                ),
                """
                    typealias generated_candid_file.AccountIdentifier = ByteArray?
                """.trimIndent()
            ),

            Arguments.of(
                IDLTypeDeclaration(
                    comment = IDLSingleLineComment(listOf("A simple typealias")),
                    id = "generated_candid_file.AccountIdentifier",
                    isOptional = true,
                    type = IDLTypeBlob()
                ),
                """
                    // A simple typealias
                    typealias generated_candid_file.AccountIdentifier = ByteArray?
                """.trimIndent()
            ),

            Arguments.of(
                IDLTypeDeclaration(
                    comment = IDLSingleLineComment(listOf("A simple typealias", "With an additional comment")),
                    id = "generated_candid_file.AccountIdentifier",
                    isOptional = true,
                    type = IDLTypeBlob()
                ),
                """
                    // A simple typealias
                    // With an additional comment
                    typealias generated_candid_file.AccountIdentifier = ByteArray?
                """.trimIndent()
            )
        )

        @JvmStatic
        fun typeRecord() = listOf(
            Arguments.of(
                IDLTypeDeclaration(
                    id = "generated_candid_file.Tokens",
                    type = IDLTypeRecord(
                        recordDeclaration = """
                            record {
                                e8s : nat64;
                            }
                        """.trimIndent()
                    )
                ),
                """
                    class generated_candid_file.Tokens (
                        val e8s : ULong
                    )
                """.trimIndent()
            )
        )

        @JvmStatic
        fun typeVariant() = listOf(
            Arguments.of(
                IDLTypeDeclaration(
                    id = "generated_candid_file.Transfer",
                    type = IDLTypeVariant(
                        variantDeclaration = """
                            variant {
                                Mint: record {
                                    to: generated_candid_file.AccountIdentifier;
                                    amount: generated_candid_file.Tokens;
                                };
                                Burn: record {
                                    from: generated_candid_file.AccountIdentifier;
                                    amount: generated_candid_file.Tokens;
                                };
                                Send: record {
                                    from: generated_candid_file.AccountIdentifier;
                                    to: generated_candid_file.AccountIdentifier;
                                    amount: generated_candid_file.Tokens;
                                };
                            }
                        """.trimIndent()
                    )
                ),
                """
                    sealed class generated_candid_file.Transfer {
                        class Mint (
                            val to : generated_candid_file.AccountIdentifier,
                            val amount : generated_candid_file.Tokens
                        ) : generated_candid_file.Transfer()
                        class Burn (
                            val from : generated_candid_file.AccountIdentifier,
                            val amount : generated_candid_file.Tokens
                        ) : generated_candid_file.Transfer()
                        class Send (
                            val from : generated_candid_file.AccountIdentifier,
                            val to : generated_candid_file.AccountIdentifier,
                            val amount : generated_candid_file.Tokens
                        ) : generated_candid_file.Transfer()
                    }
                """.trimIndent().replace("\t".toRegex(), "    ")
            )
        )
    }
}