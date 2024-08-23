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
        val kotlinClassString = KotlinClassGenerator.kotlinClass(input)
        assertEquals(expectedResult, kotlinClassString)
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("typeRecord")
    fun `type record` (
        input: IDLTypeDeclaration,
        expectedResult: String
    ) {
        val kotlinClassString = KotlinClassGenerator.kotlinClass(input)
        assertEquals(expectedResult, kotlinClassString)
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("typeVariant")
    fun `type variant` (
        input: IDLTypeDeclaration,
        expectedResult: String
    ) {
        val kotlinClassString = KotlinClassGenerator.kotlinClass(input)
        println(kotlinClassString)
        assertEquals(expectedResult, kotlinClassString)
    }

    companion object {

        @JvmStatic
        private fun typeBlob() = listOf(

            Arguments.of(
                IDLTypeDeclaration(
                    id = "AccountIdentifier",
                    type = IDLTypeBlob()
                ),
                """
                    typealias AccountIdentifier = ByteArray
                """.trimIndent()
            ),

            Arguments.of(
                IDLTypeDeclaration(
                    id = "AccountIdentifier",
                    isOptional = true,
                    type = IDLTypeBlob()
                ),
                """
                    typealias AccountIdentifier = ByteArray?
                """.trimIndent()
            ),

            Arguments.of(
                IDLTypeDeclaration(
                    comment = IDLSingleLineComment(listOf("A simple typealias")),
                    id = "AccountIdentifier",
                    isOptional = true,
                    type = IDLTypeBlob()
                ),
                """
                    // A simple typealias
                    typealias AccountIdentifier = ByteArray?
                """.trimIndent()
            ),

            Arguments.of(
                IDLTypeDeclaration(
                    comment = IDLSingleLineComment(listOf("A simple typealias", "With an additional comment")),
                    id = "AccountIdentifier",
                    isOptional = true,
                    type = IDLTypeBlob()
                ),
                """
                    // A simple typealias
                    // With an additional comment
                    typealias AccountIdentifier = ByteArray?
                """.trimIndent()
            )
        )

        @JvmStatic
        fun typeRecord() = listOf(
            Arguments.of(
                IDLTypeDeclaration(
                    id = "Tokens",
                    type = IDLTypeRecord(
                        recordDeclaration = """
                            record {
                                e8s : nat64;
                            }
                        """.trimIndent()
                    )
                ),
                """
                    class Tokens (
                        val e8s : ULong
                    )
                """.trimIndent()
            )
        )

        @JvmStatic
        fun typeVariant() = listOf(
            Arguments.of(
                IDLTypeDeclaration(
                    id = "Transfer",
                    type = IDLTypeVariant(
                        variantDeclaration = """
                            variant {
                                Mint: record {
                                    to: AccountIdentifier;
                                    amount: Tokens;
                                };
                                Burn: record {
                                    from: AccountIdentifier;
                                    amount: Tokens;
                                };
                                Send: record {
                                    from: AccountIdentifier;
                                    to: AccountIdentifier;
                                    amount: Tokens;
                                };
                            }
                        """.trimIndent()
                    )
                ),
                """
                    sealed class Transfer {
                        class Mint (
                            val to : AccountIdentifier,
                            val amount : Tokens
                        ) : Transfer()
                        class Burn (
                            val from : AccountIdentifier,
                            val amount : Tokens
                        ) : Transfer()
                        class Send (
                            val from : AccountIdentifier,
                            val to : AccountIdentifier,
                            val amount : Tokens
                        ) : Transfer()
                    }
                """.trimIndent().replace("\t".toRegex(), "    ")
            )
        )
    }
}