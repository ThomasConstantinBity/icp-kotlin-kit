package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_record.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_record.IDLRecordDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_vec.IDLVec
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

internal class CandidRecordParserTest {

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("singleRecordValue")
    fun `record with single value` (
        input: String,
        expectedResult: IDLRecordDeclaration
    ) {
        val typeDeclaration = CandidRecordParser.parseRecord(input)
        assertEquals(expectedResult, typeDeclaration)
    }

    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("multipleRecordsValue")
    fun `record with multiple values` (
        input: String,
        expectedResult: IDLRecordDeclaration
    ) {
        val typeDeclaration = CandidRecordParser.parseRecord(input)
        assertEquals(expectedResult, typeDeclaration)
    }

    companion object {

        @JvmStatic
        private fun singleRecordValue() = listOf(

            Arguments.of(
                "record { e8s : nat64; }",
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            id = "e8s",
                            type = IDLTypeNat64()
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    record { 
                        // Comment to describe value
                        e8s : nat64; 
                    }
                """.trimIndent(),
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            id = "e8s",
                            type = IDLTypeNat64(),
                            comment = IDLSingleLineComment(listOf("Comment to describe value"))
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    record { 
                        // Comment to describe value
                        // on multiple
                        // lines
                        e8s : nat64; 
                    }
                """.trimIndent(),
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            id = "e8s",
                            type = IDLTypeNat64(),
                            comment = IDLSingleLineComment(listOf("Comment to describe value", "on multiple", "lines"))
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    record {
                        e8s : nat64; // Comment to describe value
                    }
                """.trimIndent(),
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            id = "e8s",
                            type = IDLTypeNat64(),
                            comment = IDLSingleLineComment(listOf("Comment to describe value"))
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    record {
                        e8s : nat64; //         Comment with multiple spaces
                    }
                """.trimIndent(),
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            id = "e8s",
                            type = IDLTypeNat64(),
                            comment = IDLSingleLineComment(listOf("Comment with multiple spaces"))
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    record {
                        // A prefix of the requested block range.
                        // The index of the first block is equal to [generated_candid_file.GetBlocksArgs.from].
                        //
                        // Note that the number of blocks might be less than the requested
                        // [generated_candid_file.GetBlocksArgs.len] for various reasons, for example:
                        //
                        // 1. The query might have hit the replica with an outdated state
                        //    that doesn't have the full block range yet.
                        // 2. The requested range is too large to fit into a single reply.
                        //
                        // NOTE: the list of blocks can be empty if:
                        // 1. [generated_candid_file.GetBlocksArgs.len] was zero.
                        // 2. [generated_candid_file.GetBlocksArgs.from] was larger than the last block known to the canister.
                        blocks : vec generated_candid_file.Block;
                    }
                """.trimIndent(),
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            comment = IDLSingleLineComment(
                                listOf(
                                    "A prefix of the requested block range.",
                                    "The index of the first block is equal to [generated_candid_file.GetBlocksArgs.from].",
                                    "",
                                    "Note that the number of blocks might be less than the requested",
                                    "[generated_candid_file.GetBlocksArgs.len] for various reasons, for example:",
                                    "",
                                    "1. The query might have hit the replica with an outdated state",
                                    "that doesn't have the full block range yet.",
                                    "2. The requested range is too large to fit into a single reply.",
                                    "",
                                    "NOTE: the list of blocks can be empty if:",
                                    "1. [generated_candid_file.GetBlocksArgs.len] was zero.",
                                    "2. [generated_candid_file.GetBlocksArgs.from] was larger than the last block known to the canister."
                                )
                            ),
                            id = "blocks",
                            type = IDLTypeVec(
                                vecDeclaration = "vec generated_candid_file.Block"
                            )
                        )
                    )
                )
            )
        )

        @JvmStatic
        private fun multipleRecordsValue() = listOf(

            Arguments.of(
                "record { owner : principal; subaccount : opt Subaccount }",
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            id = "owner",
                            type = IDLTypePrincipal()
                        ),
                        IDLRecord(
                            id = "subaccount",
                            isOptional = true,
                            type = IDLTypeCustom("Subaccount")
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    record {
                        providerId : nat64;
                        chainId: opt nat64;
                        "service": opt RpcService;
                        primary : opt bool;
                    }
                """.trimIndent(),
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            id = "providerId",
                            type = IDLTypeNat64()
                        ),
                        IDLRecord(
                            id = "chainId",
                            isOptional = true,
                            type = IDLTypeNat64()
                        ),
                        IDLRecord(
                            id = "\"service\"",
                            isOptional = true,
                            type = IDLTypeCustom("RpcService")
                        ),
                        IDLRecord(
                            id = "primary",
                            isOptional = true,
                            type = IDLTypeBoolean()
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    record {
                        from_subaccount: opt blob; // The subaccount to transfer the token from
                        to : Account;
                        token_id : nat; // token_id comment
                        memo : opt blob;
                        created_at_time : opt nat64; // timestamp
                    }
                """.trimIndent(),
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("The subaccount to transfer the token from")),
                            id = "from_subaccount",
                            isOptional = true,
                            type = IDLTypeBlob()
                        ),
                        IDLRecord(
                            id = "to",
                            type = IDLTypeCustom("Account")
                        ),
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("token_id comment")),
                            id = "token_id",
                            type = IDLTypeNat()
                        ),
                        IDLRecord(
                            isOptional = true,
                            id = "memo",
                            type = IDLTypeBlob()
                        ),
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("timestamp")),
                            isOptional = true,
                            id = "created_at_time",
                            type = IDLTypeNat64()
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    record {
                        // generated_candid_file.Transaction memo.
                        // See comments for the `generated_candid_file.Memo` type.
                        memo: generated_candid_file.Memo;
                        // The amount that the caller wants to transfer to the destination address.
                        amount: generated_candid_file.Tokens;
                        // The amount that the caller pays for the transaction.
                        // Must be 10000 e8s.
                        fee: generated_candid_file.Tokens;
                        // The subaccount from which the caller wants to transfer funds.
                        // If null, the ledger uses the default (all zeros) subaccount to compute the source address.
                        // See comments for the `generated_candid_file.SubAccount` type.
                        from_subaccount: opt generated_candid_file.SubAccount;
                        // The destination account.
                        // If the transfer is successful, the balance of this address increases by `amount`.
                        to: generated_candid_file.AccountIdentifier;
                        // The point in time when the caller created this request.
                        // If null, the ledger uses current ICP time as the timestamp.
                        created_at_time: opt generated_candid_file.TimeStamp;
                    }
                """.trimIndent(),
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("generated_candid_file.Transaction memo.", "See comments for the `generated_candid_file.Memo` type.")),
                            id = "memo",
                            type = IDLTypeCustom("generated_candid_file.Memo")
                        ),
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("The amount that the caller wants to transfer to the destination address.")),
                            id = "amount",
                            type = IDLTypeCustom("generated_candid_file.Tokens")
                        ),
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("The amount that the caller pays for the transaction.", "Must be 10000 e8s.")),
                            id = "fee",
                            type = IDLTypeCustom("generated_candid_file.Tokens")
                        ),
                        IDLRecord(
                            comment = IDLSingleLineComment(
                                listOf(
                                    "The subaccount from which the caller wants to transfer funds.",
                                    "If null, the ledger uses the default (all zeros) subaccount to compute the source address.",
                                    "See comments for the `generated_candid_file.SubAccount` type."
                                )
                            ),
                            id = "from_subaccount",
                            isOptional = true,
                            type = IDLTypeCustom("generated_candid_file.SubAccount")
                        ),
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("The destination account.", "If the transfer is successful, the balance of this address increases by `amount`.")),
                            id = "to",
                            type = IDLTypeCustom("generated_candid_file.AccountIdentifier")
                        ),
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("The point in time when the caller created this request.", "If null, the ledger uses current ICP time as the timestamp.")),
                            id = "created_at_time",
                            isOptional = true,
                            type = IDLTypeCustom("generated_candid_file.TimeStamp")
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    record {
                        // The subaccount to transfer the token from
                        from_subaccount: opt blob;
                        to : Account;
                        // token_id comment
                        token_id : nat;
                        memo : opt blob;
                        // timestamp
                        created_at_time : opt nat64;
                    }
                """.trimIndent(),
                IDLRecordDeclaration(
                    records = listOf(
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("The subaccount to transfer the token from")),
                            id = "from_subaccount",
                            isOptional = true,
                            type = IDLTypeBlob()
                        ),
                        IDLRecord(
                            id = "to",
                            type = IDLTypeCustom("Account")
                        ),
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("token_id comment")),
                            id = "token_id",
                            type = IDLTypeNat()
                        ),
                        IDLRecord(
                            isOptional = true,
                            id = "memo",
                            type = IDLTypeBlob()
                        ),
                        IDLRecord(
                            comment = IDLSingleLineComment(listOf("timestamp")),
                            isOptional = true,
                            id = "created_at_time",
                            type = IDLTypeNat64()
                        )
                    )
                )
            )
        )
    }
}