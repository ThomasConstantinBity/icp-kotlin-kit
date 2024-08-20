package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLTypeDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFunc
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

internal class CandidTypeParserTest {

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("singleTypeDeclaration")
    fun `single types` (
        input: String,
        expectedResult: IDLTypeDeclaration
    ) {
        val typeDeclaration = CandidTypeParser.parseType(input)
        assertEquals(expectedResult, typeDeclaration)
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("funcTypeDeclaration")
    fun `func types` (
        input: String,
        expectedResult: IDLTypeDeclaration
    ) {
        val typeDeclaration = CandidTypeParser.parseType(input)
        assertEquals(expectedResult, typeDeclaration)
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("recordTypeDeclaration")
    fun `record types` (
        input: String,
        expectedResult: IDLTypeDeclaration
    ) {
        val typeDeclaration = CandidTypeParser.parseType(input)
        assertEquals(expectedResult, typeDeclaration)
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("variantTypeDeclaration")
    fun `variant types` (
        input: String,
        expectedResult: IDLTypeDeclaration
    ) {
        val typeDeclaration = CandidTypeParser.parseType(input)
        assertEquals(expectedResult, typeDeclaration)
    }


    companion object {

        @JvmStatic
        private fun singleTypeDeclaration() = listOf(
            Arguments.of(
                "type AccountIdentifier = blob;",
                IDLTypeDeclaration(
                    id = "AccountIdentifier",
                    type = IDLTypeBlob()
                )
            ),

            Arguments.of(
                "type AccountIdentifier = opt blob;",
                IDLTypeDeclaration(
                    id = "AccountIdentifier",
                    isOptional = true,
                    type = IDLTypeBlob()
                )
            ),

            Arguments.of(
                """
                    // This
                    // is
                    // a
                    // comment
                    type AccountIdentifier = opt blob;
                """.trimIndent(),
                IDLTypeDeclaration(
                    comment = IDLSingleLineComment(
                        listOf("This", "is", "a", "comment")
                    ),
                    id = "AccountIdentifier",
                    isOptional = true,
                    type = IDLTypeBlob()
                )
            ),

            Arguments.of(
                "type Memo = nat64;",
                IDLTypeDeclaration(
                    id = "Memo",
                    type = IDLTypeNat64(),
                )
            ),

            Arguments.of(
                """
                    // This is a simple comment
                    type Subaccount = blob;
                """.trimIndent(),
                IDLTypeDeclaration(
                    comment = IDLSingleLineComment(listOf("This is a simple comment")),
                    id = "Subaccount",
                    type = IDLTypeBlob()
                )
            ),
        )

        @JvmStatic
        private fun funcTypeDeclaration() = listOf(
            Arguments.of(
                "type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult) query;",
                IDLTypeDeclaration(
                    id = "QueryArchiveFn",
                    type = IDLTypeFunc(
                        funcDeclaration = "func (GetBlocksArgs) -> (QueryArchiveResult) query"
                    )
                )
            ),

            Arguments.of(
                "type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult);",
                IDLTypeDeclaration(
                    id = "QueryArchiveFn",
                    type = IDLTypeFunc(
                        funcDeclaration = "func (GetBlocksArgs) -> (QueryArchiveResult)"
                    )
                )
            ),
        )

        @JvmStatic
        private fun recordTypeDeclaration() = listOf(
            Arguments.of(
                """
                    type Tokens = record {
                        e8s : nat64;
                    };
                """.trimIndent(),
                IDLTypeDeclaration(
                    id = "Tokens",
                    type = IDLTypeRecord(
                        recordDeclaration = """
                            record {
                                e8s : nat64;
                            }
                        """.trimIndent()
                    )
                )
            ),

            Arguments.of(
                """
                    // Timestamps are represented as nanoseconds from the UNIX epoch in UTC timezone
                    type TimeStamp = record {
                        timestamp_nanos: nat64;
                    };
                """.trimIndent(),
                IDLTypeDeclaration(
                    comment = IDLSingleLineComment(listOf("Timestamps are represented as nanoseconds from the UNIX epoch in UTC timezone")),
                    id = "TimeStamp",
                    type = IDLTypeRecord("""
                        record {
                            timestamp_nanos: nat64;
                        }
                    """.trimIndent())
                )
            ),

            Arguments.of(
                """
                    type TransferArg = record {
                        from_subaccount: opt blob; // The subaccount to transfer the token from
                        to : Account;
                        token_id : nat;
                        memo : opt blob;
                        created_at_time : opt nat64;
                    };
                """.trimIndent(),
                IDLTypeDeclaration(
                    id = "TransferArg",
                    type = IDLTypeRecord("""
                        record {
                            from_subaccount: opt blob; // The subaccount to transfer the token from
                            to : Account;
                            token_id : nat;
                            memo : opt blob;
                            created_at_time : opt nat64;
                        }
                    """.trimIndent())
                )
            )
        )

        @JvmStatic
        private fun variantTypeDeclaration() = listOf(
            Arguments.of(
                """
                    type Transfer = variant {
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
                    };
                """.trimIndent(),
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
                )
            ),

            Arguments.of(
                """
                    type Transfer = variant {
                        // This is a comment
                        Mint: record {
                            // Second comment
                            to: AccountIdentifier;
                            amount: Tokens;
                        };
                    };
                """.trimIndent(),
                IDLTypeDeclaration(
                    id = "Transfer",
                    type = IDLTypeVariant(
                        variantDeclaration = """
                            variant {
                                // This is a comment
                                Mint: record {
                                    // Second comment
                                    to: AccountIdentifier;
                                    amount: Tokens;
                                };
                            }
                        """.trimIndent()
                    )
                )
            ),

            Arguments.of(
                """
                    // Generic value in accordance with ICRC-3
                    type Value = variant {
                        Blob : blob;
                        Text : text;
                        Nat : nat;
                        Int : int;
                        Array : vec Value;
                        Map : vec record { text; Value };
                    };
                """.trimIndent(),
                IDLTypeDeclaration(
                    comment = IDLSingleLineComment(listOf("Generic value in accordance with ICRC-3")),
                    id = "Value",
                    type = IDLTypeVariant("""
                        variant {
                            Blob : blob;
                            Text : text;
                            Nat : nat;
                            Int : int;
                            Array : vec Value;
                            Map : vec record { text; Value };
                        }
                    """.trimIndent())
                )
            ),

            Arguments.of(
                """
                    type TransferResult = variant {
                        Ok : nat; // Transaction index for successful transfer
                        Err : TransferError;
                    };
                """.trimIndent(),
                IDLTypeDeclaration(
                    id = "TransferResult",
                    type = IDLTypeVariant("""
                        variant {
                            Ok : nat; // Transaction index for successful transfer
                            Err : TransferError;
                        }
                    """.trimIndent())
                )
            )
        )
    }
}