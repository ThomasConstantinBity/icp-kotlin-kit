package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class CandidFileParserTest {

    @MethodSource("fileInputs")
    @ParameterizedTest
    fun `parse file input`(
        input: String,
        expectedResult: IDLFileDeclaration
    ) {
        val fileDeclaration = CandidFileParser.parseFile(input)
        assertEquals(expectedResult, fileDeclaration)
    }

    @MethodSource("candidFilePath")
    @ParameterizedTest
    fun `parse did files`(
        filePath: String,
        expectedResult: IDLFileDeclaration
    ) {
        val classLoader = this.javaClass.classLoader
        val file = File(classLoader.getResource(filePath)!!.file)
        assertTrue(file.exists())
        val fileDeclaration = CandidFileParser.parseFile(file.readText())
        assertEquals(expectedResult, fileDeclaration)
    }

    companion object {

        @JvmStatic
        private fun fileInputs() = listOf(
            Arguments.of(
                """
                    type Subaccount = blob;
                    service : {
                        icrc7_token_metadata : (token_ids : vec nat) -> (vec opt vec record { text; Value }) query;
                    }
                """.trimIndent(),
                IDLFileDeclaration(
                    types = listOf(
                        IDLFileType(
                            typeDefinition = "type Subaccount = blob;"
                        )
                    ),
                    service = IDLFileService(
                        serviceDefinition = """
                            service : {
                                icrc7_token_metadata : (token_ids : vec nat) -> (vec opt vec record { text; Value }) query;
                            }
                        """.trimIndent()
                    )
                )
            )
        )

        @JvmStatic
        private fun candidFilePath() = listOf(
            Arguments.of(
                "candid_file/LedgerCanister.did",
                IDLFileDeclaration(
                    comment = IDLSingleLineComment(listOf("https://internetcomputer.org/docs/current/references/ledger/")),
                    types = listOf(
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.Tokens = record {
                                     e8s : nat64;
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            comment = IDLSingleLineComment(
                                listOf(
                                    "Account identifier is a 32-byte array.",
                                    "The first 4 bytes is big-endian encoding of a CRC32 checksum of the last 28 bytes"
                                )
                            ),
                            typeDefinition = "type generated_candid_file.AccountIdentifier = blob;"
                        ),
                        IDLFileType(
                            comment = IDLSingleLineComment(listOf("There are three types of operations: minting tokens, burning tokens & transferring tokens")),
                            typeDefinition = """
                                type generated_candid_file.Transfer = variant {
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
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = "type generated_candid_file.Memo = nat64;"
                        ),
                        IDLFileType(
                            typeDefinition = "type generated_candid_file.SubAccount = blob;"
                        ),
                        IDLFileType(
                            typeDefinition = "type Hash = blob;"
                        ),
                        IDLFileType(
                            comment = IDLSingleLineComment(listOf("Timestamps are represented as nanoseconds from the UNIX epoch in UTC timezone")),
                            typeDefinition = """
                                type generated_candid_file.TimeStamp = record {
                                    timestamp_nanos: nat64;
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.Transaction = record {
                                    operation: opt generated_candid_file.Transfer;
                                    memo: generated_candid_file.Memo;
                                    created_at_time: generated_candid_file.TimeStamp;
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.Block = record {
                                    parent_hash: opt Hash;
                                    transaction: generated_candid_file.Transaction;
                                    timestamp: generated_candid_file.TimeStamp;
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = "type generated_candid_file.BlockIndex = nat64;"
                        ),
                        IDLFileType(
                            comment = IDLSingleLineComment(listOf("The ledger is a list of blocks")),
                            typeDefinition = "type generated_candid_file.Ledger = vec generated_candid_file.Block;"
                        ),
                        IDLFileType(
                            comment = IDLSingleLineComment(listOf("Arguments for the `transfer` call.")),
                            typeDefinition = """
                                type generated_candid_file.TransferArgs = record {
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
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.TransferError = variant {
                                    // The fee that the caller specified in the transfer request was not the one that ledger expects.
                                    // The caller can change the transfer fee to the `expected_fee` and retry the request.
                                    BadFee : record { expected_fee : generated_candid_file.Tokens; };
                                    // The account specified by the caller doesn't have enough funds.
                                    InsufficientFunds : record { balance: generated_candid_file.Tokens; };
                                    // The request is too old.
                                    // The ledger only accepts requests created within 24 hours window.
                                    // This is a non-recoverable error.
                                    TxTooOld : record { allowed_window_nanos: nat64 };
                                    // The caller specified `created_at_time` that is too far in future.
                                    // The caller can retry the request later.
                                    TxCreatedInFuture : null;
                                    // The ledger has already executed the request.
                                    // `duplicate_of` field is equal to the index of the block containing the original transaction.
                                    TxDuplicate : record { duplicate_of: generated_candid_file.BlockIndex; }
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.TransferResult = variant {
                                    Ok : generated_candid_file.BlockIndex;
                                    Err : generated_candid_file.TransferError;
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.GetBlocksArgs = record {
                                    // The index of the first block to fetch.
                                    start : generated_candid_file.BlockIndex;
                                    // Max number of blocks to fetch.
                                    length : nat64;
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            comment = IDLSingleLineComment(listOf("A prefix of the block range specified in the [generated_candid_file.GetBlocksArgs] request.")),
                            typeDefinition = """
                                type generated_candid_file.BlockRange = record {
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
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.QueryArchiveResult = variant {
                                    Ok : generated_candid_file.BlockRange;
                                    Err : null;      // we don't know the values here...
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            comment = IDLSingleLineComment(listOf("A function that is used for fetching archived ledger blocks.")),
                            typeDefinition = "type generated_candid_file.QueryArchiveFn = func (generated_candid_file.GetBlocksArgs) -> (generated_candid_file.QueryArchiveResult) query;"
                        ),
                        IDLFileType(
                            comment = IDLSingleLineComment(
                                listOf(
                                    "The result of a \"query_blocks\" call.",
                                    "",
                                    "The structure of the result is somewhat complicated because the main ledger canister might",
                                    "not have all the blocks that the caller requested: One or more \"archive\" canisters might",
                                    "store some of the requested blocks.",
                                    "",
                                    "Note: as of Q4 2021 when this interface is authored, ICP doesn't support making nested",
                                    "query calls within a query call."
                                )
                            ),
                            typeDefinition = """
                                type generated_candid_file.QueryBlocksResponse = record {
                                    // The total number of blocks in the chain.
                                    // If the chain length is positive, the index of the last block is `chain_len - 1`.
                                    chain_length : nat64;

                                    // System certificate for the hash of the latest block in the chain.
                                    // Only present if `query_blocks` is called in a non-replicated query context.
                                    certificate : opt blob;

                                    // List of blocks that were available in the ledger when it processed the call.
                                    //
                                    // The blocks form a contiguous range, with the first block having index
                                    // [first_block_index] (see below), and the last block having index
                                    // [first_block_index] + len(blocks) - 1.
                                    //
                                    // The block range can be an arbitrary sub-range of the originally requested range.
                                    blocks : vec generated_candid_file.Block;

                                    // The index of the first block in "blocks".
                                    // If the blocks vector is empty, the exact value of this field is not specified.
                                    first_block_index : generated_candid_file.BlockIndex;

                                    // Encoding of instructions for fetching archived blocks whose indices fall into the
                                    // requested range.
                                    //
                                    // For each entry `e` in [archived_blocks], `[e.from, e.from + len)` is a sub-range
                                    // of the originally requested block range.
                                    archived_blocks : vec record {
                                        // The index of the first archived block that can be fetched using the callback.
                                        start : generated_candid_file.BlockIndex;

                                        // The number of blocks that can be fetched using the callback.
                                        length : nat64;

                                        // The function that should be called to fetch the archived blocks.
                                        // The range of the blocks accessible using this function is given by [from]
                                        // and [len] fields above.
                                        callback : generated_candid_file.QueryArchiveFn;
                                    };
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.Archive = record {
                                    canister_id: principal;
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.Archives = record {
                                    archives: vec generated_candid_file.Archive;
                                };
                            """.trimIndent()
                        ),
                        IDLFileType(
                            typeDefinition = """
                                type generated_candid_file.AccountBalanceArgs = record {
                                    account: generated_candid_file.AccountIdentifier;
                                };
                            """.trimIndent()
                        ),
                    ),
                    service = IDLFileService(
                        serviceDefinition = """
                            service : {
                              // Queries blocks in the specified range.
                              query_blocks : (generated_candid_file.GetBlocksArgs) -> (generated_candid_file.QueryBlocksResponse) query;

                              // Returns the existing archive canisters information.
                              archives : () -> (generated_candid_file.Archives) query;

                              // Get the amount of ICP on the specified account.
                              account_balance : (generated_candid_file.AccountBalanceArgs) -> (generated_candid_file.Tokens) query;

                              transfer : (generated_candid_file.TransferArgs) -> (generated_candid_file.TransferResult);
                            }
                        """.trimIndent()
                    )
                )
            )
        )
    }
}