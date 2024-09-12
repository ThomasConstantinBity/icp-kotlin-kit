package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

internal class CandidFileParserTest {

    @MethodSource("vec")
    @ParameterizedTest
    fun `parse vec`(
        input: String,
        expectedResult: IDLFileDeclaration
    ) {
        val fileDeclaration = CandidFileParser.parseFile(input)
        assertEquals(expectedResult, fileDeclaration)
    }

    @MethodSource("record")
    @ParameterizedTest
    fun `parse record`(
        input: String,
        expectedResult: IDLFileDeclaration
    ) {
        val fileDeclaration = CandidFileParser.parseFile(input)
        assertEquals(expectedResult, fileDeclaration)
    }

    @MethodSource("typeAlias")
    @ParameterizedTest
    fun `type alias`(
        input: String,
        expectedResult: IDLFileDeclaration
    ) {
        val fileDeclaration = CandidFileParser.parseFile(input)
        assertEquals(expectedResult, fileDeclaration)
    }

    @MethodSource("variant")
    @ParameterizedTest
    fun `parse variant`(
        input: String,
        expectedResult: IDLFileDeclaration
    ) {
        val fileDeclaration = CandidFileParser.parseFile(input)
        assertEquals(expectedResult, fileDeclaration)
    }

    companion object {

        @JvmStatic
        private fun vec() = listOf(
            Arguments.of(
                "type Ledger = vec Block;",
                IDLFileDeclaration(
                    types = listOf(
                        IDLTypeVec(
                            vecDeclaration = "Ledger",
                            vecType = IDLTypeCustom(
                                typeDef = "Block"
                            )
                        )
                    )
                )
            )
        )

        @JvmStatic
        private fun record() = listOf(
            Arguments.of(
                """
                    type Tokens = record {
                        e8s : nat64;
                    };
                """.trimIndent(),
                IDLFileDeclaration(
                    types = listOf(
                        IDLRecord(
                            recordName = "Tokens",
                            types = listOf(
                                IDLTypeNat64(
                                    id = "e8s"
                                )
                            )
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    type TimeStamp = record {
                        timestamp_nanos: nat64;
                    };
                """.trimIndent(),
                IDLFileDeclaration(
                    types = listOf(
                        IDLRecord(
                            recordName = "TimeStamp",
                            types = listOf(
                                IDLTypeNat64(
                                    id = "timestamp_nanos"
                                )
                            )
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    type Transaction = record {
                        operation: opt Transfer;
                        memo: Memo;
                        created_at_time: TimeStamp;
                    };
                """.trimIndent(),
                IDLFileDeclaration(
                    types = listOf(
                        IDLRecord(
                            recordName = "Transaction",
                            types = listOf(
                                IDLTypeCustom(
                                    id = "operation",
                                    isOptional = true,
                                    typeDef = "Transfer"
                                ),
                                IDLTypeCustom(
                                    id = "memo",
                                    typeDef = "Memo"
                                ),
                                IDLTypeCustom(
                                    id = "created_at_time",
                                    typeDef = "TimeStamp"
                                )
                            )
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    type Block = record {
                        parent_hash: opt Hash;
                        transaction: Transaction;
                        timestamp: TimeStamp;
                    };
                """.trimIndent(),
                IDLFileDeclaration(
                    types = listOf(
                        IDLRecord(
                            recordName = "Block",
                            types = listOf(
                                IDLTypeCustom(
                                    id = "parent_hash",
                                    isOptional = true,
                                    typeDef = "Hash"
                                ),
                                IDLTypeCustom(
                                    id = "transaction",
                                    typeDef = "Transaction"
                                ),
                                IDLTypeCustom(
                                    id = "timestamp",
                                    typeDef = "TimeStamp"
                                )
                            )
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    type TransferArgs = record {
                        // Transaction memo.
                        // See comments for the `Memo` type.
                        memo: Memo;
                    };
                """.trimIndent(),
                IDLFileDeclaration(
                    types = listOf(
                        IDLRecord(
                            recordName = "TransferArgs",
                            types = listOf(
                                IDLTypeCustom(
                                    comment = IDLSingleLineComment(
                                        listOf("Transaction memo.",
                                            "See comments for the `Memo` type."
                                        )
                                    ),
                                    id = "memo",
                                    typeDef = "Memo"
                                )
                            )
                        )
                    )
                )
            )
        )

        @JvmStatic
        private fun typeAlias() = listOf(
            Arguments.of(
                "type AccountIdentifier = blob;",
                IDLFileDeclaration(
                    types = listOf(
                        IDLTypeCustom(
                            typeDef = "AccountIdentifier",
                            type = IDLTypeBlob()
                        )
                    )
                )
            ),

            Arguments.of(
                "type Memo = nat64;",
                IDLFileDeclaration(
                    types = listOf(
                        IDLTypeCustom(
                            typeDef = "Memo",
                            type = IDLTypeNat64()
                        )
                    )
                )
            ),

            Arguments.of(
                "type SubAccount = blob;",
                IDLFileDeclaration(
                    types = listOf(
                        IDLTypeCustom(
                            typeDef = "SubAccount",
                            type = IDLTypeBlob()
                        )
                    )
                )
            ),

            Arguments.of(
                "type Hash = blob;",
                IDLFileDeclaration(
                    types = listOf(
                        IDLTypeCustom(
                            typeDef = "Hash",
                            type = IDLTypeBlob()
                        )
                    )
                )
            ),

            Arguments.of(
                "type BlockIndex = nat64;",
                IDLFileDeclaration(
                    types = listOf(
                        IDLTypeCustom(
                            typeDef = "BlockIndex",
                            type = IDLTypeNat64()
                        )
                    )
                )
            )
        )

        @JvmStatic
        private fun variant() = listOf(
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
                IDLFileDeclaration(
                    types = listOf(
                        IDLTypeVariant(
                            id = "Transfer",
                            records = listOf(
                                IDLRecord(
                                    recordName = "Mint",
                                    types = listOf(
                                        IDLTypeCustom(
                                            id = "to",
                                            typeDef = "AccountIdentifier"
                                        ),
                                        IDLTypeCustom(
                                            id = "amount",
                                            typeDef = "Tokens"
                                        )
                                    )
                                ),
                                IDLRecord(
                                    recordName = "Burn",
                                    types = listOf(
                                        IDLTypeCustom(
                                            id = "from",
                                            typeDef = "AccountIdentifier"
                                        ),
                                        IDLTypeCustom(
                                            id = "amount",
                                            typeDef = "Tokens"
                                        )
                                    )
                                ),
                                IDLRecord(
                                    recordName = "Send",
                                    types = listOf(
                                        IDLTypeCustom(
                                            id = "from",
                                            typeDef = "AccountIdentifier"
                                        ),
                                        IDLTypeCustom(
                                            id = "to",
                                            typeDef = "AccountIdentifier"
                                        ),
                                        IDLTypeCustom(
                                            id = "amount",
                                            typeDef = "Tokens"
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}