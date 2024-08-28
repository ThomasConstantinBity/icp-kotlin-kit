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
                        Ok : generated_candid_file.BlockRange;
                        Err : null;      // we don't know the values here...
                    };
                """.trimIndent(),
                IDLVariantDeclaration(
                    variants = listOf(
                        IDLVariant(
                            id = "Ok",
                            type = IDLTypeCustom("generated_candid_file.BlockRange")
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
                """.trimIndent(),
                IDLVariantDeclaration(
                    variants = listOf(
                        IDLVariant(
                            id = "Mint",
                            type = IDLTypeRecord(
                                recordDeclaration = """
                                    record {
                                        to: generated_candid_file.AccountIdentifier;
                                        amount: generated_candid_file.Tokens;
                                    }
                                """.trimIndent()
                            )
                        ),
                        IDLVariant(
                            id = "Burn",
                            type = IDLTypeRecord(
                                recordDeclaration = """
                                   record {
                                       from: generated_candid_file.AccountIdentifier;
                                       amount: generated_candid_file.Tokens;
                                   }
                                """.trimIndent()
                            )
                        ),
                        IDLVariant(
                            id = "Send",
                            type = IDLTypeRecord(
                                recordDeclaration = """
                                    record {
                                        from: generated_candid_file.AccountIdentifier;
                                        to: generated_candid_file.AccountIdentifier;
                                        amount: generated_candid_file.Tokens;
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