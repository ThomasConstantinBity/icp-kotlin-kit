package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_variant.IDLVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_variant.IDLVariantDeclaration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class CandidVariantParserTest {

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("variantValue")
    fun `parse variant` (
        input: String,
        expectedResult: IDLVariantDeclaration
    ) {
        val typeDeclaration = CandidVariantParser.parseVariant(input)
        assertEquals(expectedResult, typeDeclaration)
    }

    companion object {
        @JvmStatic
        private fun variantValue() = listOf(
            Arguments.of(
                """
                    variant {
                        Ok : BlockRange;
                        Err : null;      // we don't know the values here...
                    };
                """.trimIndent(),
                IDLVariantDeclaration(
                    variants = listOf(
                        IDLVariant(
                            id = "Ok",
                            type = IDLTypeCustom("BlockRange")
                        ),
                        IDLVariant(
                            comment = IDLSingleLineComment(listOf("we don't know the values here...")),
                            id = "Err",
                            type = IDLTypeNull()
                        )
                    )
                )
            ),

            Arguments.of(
                """
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
                """.trimIndent(),
                IDLVariantDeclaration(
                    variants = listOf(
                        IDLVariant(
                            id = "Mint",
                            type = IDLTypeRecord(
                                recordDeclaration = """
                                    record {
                                        to: AccountIdentifier;
                                        amount: Tokens;
                                    }
                                """.trimIndent()
                            )
                        ),
                        IDLVariant(
                            id = "Burn",
                            type = IDLTypeRecord(
                                recordDeclaration = """
                                   record {
                                       from: AccountIdentifier;
                                       amount: Tokens;
                                   }
                                """.trimIndent()
                            )
                        ),
                        IDLVariant(
                            id = "Send",
                            type = IDLTypeRecord(
                                recordDeclaration = """
                                    record {
                                        from: AccountIdentifier;
                                        to: AccountIdentifier;
                                        amount: Tokens;
                                    }
                                """.trimIndent()
                            )
                        )
                    )
                )
            )
        )
    }
}