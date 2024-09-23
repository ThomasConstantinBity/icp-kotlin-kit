package com.bity.icp_kotlin_kit.domain.usecase

import com.bity.icp_kotlin_kit.candid.CandidDecoder
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.request.PollingValues

/**
 * File generated using ICP Kotlin Kit Plugin
 */

/**
 * type AccountIdentifier = blob;
 */
// Account identifier  is a 32-byte array.
// The first 4 bytes is big-endian encoding of a CRC32 checksum of the last 28 bytes
typealias AccountIdentifier = ByteArray

/**
 * type Memo = nat64;
 */
typealias Memo = ULong

/**
 * type SubAccount = blob;
 */
typealias SubAccount = ByteArray

/**
 * type Hash = blob;
 */
typealias Hash = ByteArray

/**
 * type BlockIndex = nat64;
 */
typealias BlockIndex = ULong

/**
 * type Ledger = vec Block;
 */
// The ledger is a list of blocks
typealias Ledger = Array<LedgerCanister.Block>


object LedgerCanister {

    /**
     * type Tokens = record {
     *     e8s : nat64;
     * };
     */
    data class Tokens(
        val e8s: ULong
    )

    /**
     * type Operation = variant {
     *         Mint: record {
     *             to: AccountIdentifier;
     *             amount: Tokens;
     *         };
     *         Burn: record {
     *             from: AccountIdentifier;
     *             amount: Tokens;
     *             spender : opt blob
     *         };
     *         Transfer: record {
     *             from: AccountIdentifier;
     *             to: AccountIdentifier;
     *             amount: Tokens;
     *             fee: Tokens;
     *             spender : opt blob;
     *         };
     *     };
     */
    //There are three types of operations: minting tokens, burning tokens & transferring tokens
    sealed class Operation {

        data class Mint(
            val to: AccountIdentifier,
            val amount: Tokens
        ): Operation()

        data class Burn(
            val from: AccountIdentifier,
            val amount: Tokens,
            val spender: ByteArray?
        ): Operation()

        data class Transfer(
            val from: AccountIdentifier,
            val to: AccountIdentifier,
            val amount: Tokens,
            val fee: Tokens,
            val spender: ByteArray?
        ): Operation()

    }

    /**
     * type TimeStamp = record {
     *     timestamp_nanos: nat64;
     * };
     */
    // Timestamps are represented as nanoseconds from the UNIX epoch in UTC timezone
    data class TimeStamp(
        val timestamp_nanos: ULong
    )

    /**
     * type Transaction = record {
     *     operation: opt Operation;
     *     memo: Memo;
     *     created_at_time: TimeStamp;
     * };
     */
    data class Transaction(
        val operation: Operation?,
        val memo: Memo,
        val created_at_time: TimeStamp
    )

    /**
     * type Block = record {
     *     parent_hash: opt Hash;
     *     transaction: Transaction;
     *     timestamp: TimeStamp;
     * };
     */
    data class Block(
        val parent_hash: Hash?,
        val transaction: Transaction,
        val timestamp: TimeStamp
    )

    /**
     * type TransferArgs = record {
     *     memo: Memo;
     *     amount: Tokens;
     *     fee: Tokens;
     *     from_subaccount: opt SubAccount;
     *     to: AccountIdentifier;
     *     created_at_time: opt TimeStamp;
     * };
     */
    // Arguments for the `transfer` call.
    class TransferArgs(
        // Transaction memo.
        // See comments for the `Memo` type.
        val memo: Memo,

        // The amount that the caller wants to transfer to the destination address.
        val amount: Tokens,

        // The amount that the caller pays for the transaction.
        // Must be 10000 e8s.
        val fee: Tokens,

        // The subaccount from which the caller wants to transfer funds.
        // If null, the ledger uses the default (all zeros) subaccount to compute the source address.
        // See comments for the `SubAccount` type.
        val from_subaccount: SubAccount?,

        // The destination account.
        // If the transfer is successful, the balance of this address increases by `amount`.
        val to: AccountIdentifier,

        // The point in time when the caller created this request.
        // If null, the ledger uses current ICP time as the timestamp.
        val created_at_time: TimeStamp?
    )

    /**
     * type TransferError = variant {
     *     BadFee : record { expected_fee : Tokens; };
     *     InsufficientFunds : record { balance: Tokens; };
     *     TxTooOld : record { allowed_window_nanos: nat64 };
     *     TxCreatedInFuture : null;
     *     TxDuplicate : record { duplicate_of: BlockIndex; }
     * };
     */
    sealed class TransferError {

        // The fee that the caller specified in the transfer request was not the one that ledger expects.
        // The caller can change the transfer fee to the `expected_fee` and retry the request.
        class BadFee(
            val expected_fee: Tokens
        ): TransferError()

        // The account specified by the caller doesn't have enough funds.
        class InsufficientFunds(
            val balance: Tokens
        ): TransferError()

        // The request is too old.
        // The ledger only accepts requests created within 24 hours window.
        // This is a non-recoverable error.
        class TxTooOld(
            val allowed_window_nanos: ULong
        ): TransferError()

        // The caller specified `created_at_time` that is too far in future.
        // The caller can retry the request later.
        data object TxCreatedInFuture : TransferError()

        // The ledger has already executed the request.
        // `duplicate_of` field is equal to the index of the block containing the original transaction.
        class TxDuplicate(
            val duplicate_of: BlockIndex
        ): TransferError()
    }

    /**
     * type TransferResult = variant {
     *     Ok : BlockIndex;
     *     Err : TransferError;
     * };
     */
    sealed class TransferResult {

        class Ok(
            val blockIndex: BlockIndex
        ): TransferResult()

        class Err(
            val transferError: TransferError
        ): TransferResult()
    }

    /**
     * type GetBlocksArgs = record {
     *     start : BlockIndex;
     *     length : nat64;
     * };
     */
    class GetBlocksArgs(
        // The index of the first block to fetch.
        val start: BlockIndex,
        // Max number of blocks to fetch.
        val length: ULong
    )

    /**
     * type BlockRange = record {
     *     blocks : vec Block;
     * };
     */
    // A prefix of the block range specified in the [GetBlocksArgs] request.
    data class BlockRange(
        // A prefix of the requested block range.
        // The index of the first block is equal to [GetBlocksArgs.from].
        //
        // Note that the number of blocks might be less than the requested
        // [GetBlocksArgs.len] for various reasons, for example:
        //
        // 1. The query might have hit the replica with an outdated state
        // that doesn't have the full block range yet.
        // 2. The requested range is too large to fit into a single reply.
        //
        // NOTE: the list of blocks can be empty if:
        // 1. [GetBlocksArgs.len] was zero.
        // 2. [GetBlocksArgs.from] was larger than the last block known to the canister.
        val blocks: Array<Block>
    )

    /**
     * type QueryArchiveResult = variant {
     *     Ok : BlockRange;
     *     Err : null;
     * };
     */
    sealed class QueryArchiveResult {

        data class Ok(
            val blockRange: BlockRange
        ): QueryArchiveResult()

        // we don't know the values here...
        data object Err : QueryArchiveResult()
    }

    /**
     * type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult) query;
     */
    // A function that is used for fetching archived ledger blocks.
    class QueryArchiveFn(
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
        ): QueryArchiveResult {
            val result = query(
                args = args,
                certification = certification,
                sender = sender,
                pollingValues = pollingValues
            ).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }
    }

    /**
     * type QueryBlocksResponse = record {
     *     chain_length : nat64;
     *     certificate : opt blob;
     *     blocks : vec Block;
     *     archived_blocks : vec record {
     *         start : BlockIndex;
     *         length : nat64;
     *         callback : QueryArchiveFn;
     *     };
     * };
     */
    // The result of a "query_blocks" call.
    //
    // The structure of the result is somewhat complicated because the main ledger canister might
    // not have all the blocks that the caller requested: One or more "archive" canisters might
    // store some of the requested blocks.
    //
    // Note: as of Q4 2021 when this interface is authored, ICP doesn't support making nested
    // query calls within a query call.
    class QueryBlocksResponse(
        // The total number of blocks in the chain.
        // If the chain length is positive, the index of the last block is `chain_len - 1`.
        val chain_length: ULong,

        // List of blocks that were available in the ledger when it processed the call.
        //
        // The blocks form a contiguous range, with the first block having index
        // [first_block_index] (see below), and the last block having index
        // [first_block_index] + len(blocks) - 1.
        //
        // The block range can be an arbitrary sub-range of the originally requested range.
        val certificate: ByteArray?,
        val blocks: Array<Block>,

        // The index of the first block in "blocks".
        // If the blocks vector is empty, the exact value of this field is not specified.
        val first_block_index: BlockIndex,

        // Encoding of instructions for fetching archived blocks whose indices fall into the
        // requested range.
        //
        // For each entry `e` in [archived_blocks], `[e.from, e.from + len)` is a sub-range
        // of the originally requested block range.
        val archived_blocks: Array<_Class1>
    ) {
        class _Class1(
            // The index of the first archived block that can be fetched using the callback.
            val start: BlockIndex,

            // The number of blocks that can be fetched using the callback.
            val length: ULong,

            // The function that should be called to fetch the archived blocks.
            // The range of the blocks accessible using this function is given by [from]
            // and [len] fields above.
            val callback: QueryArchiveFn
        )
    }

    /**
     * type Archive = record {
     *     canister_id: principal;
     * };
     */
    class Archive(
        val canister_id: ICPPrincipal
    )

    /**
     * type Archives = record {
     *     archives: vec Archive;
     * };
     */
    class Archives(
        val archives: Array<Archive>
    )

    /**
     * type AccountBalanceArgs = record {
     *     account: AccountIdentifier;
     * };
     */
    class AccountBalanceArgs(
        val account: AccountIdentifier
    )

    class LedgerCanisterService(
        private val canister: ICPPrincipal
    ) {

        /**
         * query_blocks : (GetBlocksArgs) -> (QueryBlocksResponse) query;
         */
        // Queries blocks in the specified range.
        suspend fun query_blocks (
            getBlocksArgs: GetBlocksArgs,
            certification: ICPRequestCertification = ICPRequestCertification.Uncertified,
            sender: ICPSigningPrincipal? = null,
            pollingValues: PollingValues = PollingValues()
        ): QueryBlocksResponse {
            val icpQuery = ICPQuery(
                methodName = "query_blocks",
                canister = canister
            )
            val result = icpQuery.query(
                args = listOf(getBlocksArgs),
                certification = certification,
                sender = sender,
                pollingValues = pollingValues
            ).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }

        /**
         * archives : () -> (Archives) query;
         */
        // Returns the existing archive canisters information.
        suspend fun archives (
            certification: ICPRequestCertification = ICPRequestCertification.Uncertified,
            sender: ICPSigningPrincipal? = null,
            pollingValues: PollingValues = PollingValues()
        ): Archives {
            val icpQuery = ICPQuery(
                methodName = "archives",
                canister = canister
            )
            val result = icpQuery.query(
                args = listOf(),
                certification = certification,
                sender = sender,
                pollingValues = pollingValues
            ).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }

        /**
         * account_balance : (AccountBalanceArgs) -> (Tokens) query;
         */
        // Get the amount of ICP on the specified account.
        suspend fun account_balance (
            accountBalanceArgs: AccountBalanceArgs,
            certification: ICPRequestCertification = ICPRequestCertification.Uncertified,
            sender: ICPSigningPrincipal? = null,
            pollingValues: PollingValues = PollingValues()
        ): Tokens {
            val icpQuery = ICPQuery(
                methodName = "account_balance",
                canister = canister
            )
            val result = icpQuery.query(
                args = listOf(accountBalanceArgs),
                certification = certification,
                sender = sender,
                pollingValues = pollingValues
            ).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }

        /**
         * transfer : (TransferArgs) -> (TransferResult);
         */
        suspend fun transfer (
            transferArgs: TransferArgs,
            certification: ICPRequestCertification = ICPRequestCertification.Uncertified,
            sender: ICPSigningPrincipal? = null,
            pollingValues: PollingValues = PollingValues()
        ): TransferResult {
            val icpQuery = ICPQuery(
                methodName = "transfer",
                canister = canister
            )
            val result = icpQuery.query(
                args = listOf(transferArgs),
                certification = certification,
                sender = sender,
                pollingValues = pollingValues
            ).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }
    }
}