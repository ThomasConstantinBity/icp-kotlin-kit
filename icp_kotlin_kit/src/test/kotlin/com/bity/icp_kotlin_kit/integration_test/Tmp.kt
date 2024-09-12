package com.bity.icp_kotlin_kit.integration_test

// TODO, add package name

import com.bity.icp_kotlin_kit.candid.CandidDecoder
import com.bity.icp_kotlin_kit.candid.CandidEncoder
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.candid.model.CandidVector
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.bity.icp_kotlin_kit.domain.request.PollingValues
import com.bity.icp_kotlin_kit.domain.usecase.ICPQuery
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.shared.NTuple3
import com.bity.icp_kotlin_kit.provideICPCanisterRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * type AccountIdentifier = blob;
 */
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
typealias Ledger = LedgerCanister.Block

// https://internetcomputer.org/docs/current/references/ledger/
object LedgerCanister {

    /**
     * type Tokens = record {
     *     e8s : nat64;
     * };
     */
    data class Tokens (
        val e8s: ULong
    )

    /**
     * type Transfer = variant {
     *     Mint: record {
     *         to: AccountIdentifier;
     *         amount: Tokens;
     *     };
     *     Burn: record {
     *         from: AccountIdentifier;
     *         amount: Tokens;
     *     };
     *     Send: record {
     *         from: AccountIdentifier;
     *         to: AccountIdentifier;
     *         amount: Tokens;
     *     };
     * };
     */
    sealed class Transfer {
        data class Mint (
            val to: AccountIdentifier,
            val amount: Tokens
        ) : Transfer()
        data class Burn (
            val from: AccountIdentifier,
            val amount: Tokens
        ) : Transfer()
        data class Send (
            val from: AccountIdentifier,
            val to: AccountIdentifier,
            val amount: Tokens
        ) : Transfer()
    }

    /**
     * type TimeStamp = record {
     *     timestamp_nanos: nat64;
     * };
     */
    data class TimeStamp (
        val timestamp_nanos: ULong
    )

    /**
     * type Transaction = record {
     *     operation: opt Transfer;
     *     memo: Memo;
     *     created_at_time: TimeStamp;
     * };
     */
    data class Transaction (
        val operation: Transfer?,
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
    data class Block (
        val parent_hash: Hash?,
        val transaction: Transaction,
        val timestamp: TimeStamp
    )

    /**
     * type TransferArgs = record {
     *     // Transaction memo.
     *     // See comments for the `Memo` type.
     *     memo: Memo;
     *     // The amount that the caller wants to transfer to the destination address.
     *     amount: Tokens;
     *     // The amount that the caller pays for the transaction.
     *     // Must be 10000 e8s.
     *     fee: Tokens;
     *     // The subaccount from which the caller wants to transfer funds.
     *     // If null, the ledger uses the default (all zeros) subaccount to compute the source address.
     *     // See comments for the `SubAccount` type.
     *     from_subaccount: opt SubAccount;
     *     // The destination account.
     *     // If the transfer is successful, the balance of this address increases by `amount`.
     *     to: AccountIdentifier;
     *     // The point in time when the caller created this request.
     *     // If null, the ledger uses current ICP time as the timestamp.
     *     created_at_time: opt TimeStamp;
     * };
     */
    data class TransferArgs (
        // The amount that the caller wants to transfer to the destination address.
        val memo: Memo,
        // The amount that the caller pays for the transaction.
        // Must be 10000 e8s.
        val amount: Tokens,
        // The subaccount from which the caller wants to transfer funds.
        // If null, the ledger uses the default (all zeros) subaccount to compute the source address.
        // See comments for the `SubAccount` type.
        val fee: Tokens,
        // The destination account.
        // If the transfer is successful, the balance of this address increases by `amount`.
        val from_subaccount: SubAccount?,
        // The point in time when the caller created this request.
        // If null, the ledger uses current ICP time as the timestamp.
        val to: AccountIdentifier,
        val created_at_time: TimeStamp?
    )

    /**
     * type TransferError = variant {
     *     // The fee that the caller specified in the transfer request was not the one that ledger expects.
     *     // The caller can change the transfer fee to the `expected_fee` and retry the request.
     *     BadFee : record { expected_fee : Tokens; };
     *     // The account specified by the caller doesn't have enough funds.
     *     InsufficientFunds : record { balance: Tokens; };
     *     // The request is too old.
     *     // The ledger only accepts requests created within 24 hours window.
     *     // This is a non-recoverable error.
     *     TxTooOld : record { allowed_window_nanos: nat64 };
     *     // The caller specified `created_at_time` that is too far in future.
     *     // The caller can retry the request later.
     *     TxCreatedInFuture : null;
     *     // The ledger has already executed the request.
     *     // `duplicate_of` field is equal to the index of the block containing the original transaction.
     *     TxDuplicate : record { duplicate_of: BlockIndex; }
     * };
     */
    sealed class TransferError {
        data class BadFee (
            val expected_fee: Tokens
        ) : TransferError()
        data class InsufficientFunds (
            val balance: Tokens
        ) : TransferError()
        data class TxTooOld (
            val allowed_window_nanos: ULong
        ) : TransferError()
        data object TxCreatedInFuture : TransferError()
        data class TxDuplicate (
            val duplicate_of: BlockIndex
        ) : TransferError()
    }

    /**
     * type TransferResult = variant {
     *     Ok : BlockIndex;
     *     Err : TransferError;
     * };
     */
    sealed class TransferResult {
        data class Ok (
            val blockIndex: BlockIndex
        ) : TransferResult()
        data class Err (
            val transferError: TransferError
        ) : TransferResult()
    }

    /**
     * type GetBlocksArgs = record {
     *     // The index of the first block to fetch.
     *     start : BlockIndex;
     *     // Max number of blocks to fetch.
     *     length : nat64;
     * };
     */
    data class GetBlocksArgs (
        // Max number of blocks to fetch.
        val start: BlockIndex,
        val length: ULong
    )

    /**
     * type BlockRange = record {
     *     // A prefix of the requested block range.
     *     // The index of the first block is equal to [GetBlocksArgs.from].
     *     //
     *     // Note that the number of blocks might be less than the requested
     *     // [GetBlocksArgs.len] for various reasons, for example:
     *     //
     *     // 1. The query might have hit the replica with an outdated state
     *     //    that doesn't have the full block range yet.
     *     // 2. The requested range is too large to fit into a single reply.
     *     //
     *     // NOTE: the list of blocks can be empty if:
     *     // 1. [GetBlocksArgs.len] was zero.
     *     // 2. [GetBlocksArgs.from] was larger than the last block known to the canister.
     *     blocks : vec Block;
     * };
     */
    data class BlockRange (
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
     *     Err : null;      // we don't know the values here...
     * };
     */
    sealed class QueryArchiveResult {
        data class Ok (
            val blockRange: BlockRange
        ) : QueryArchiveResult()
        data object Err : QueryArchiveResult()
    }

    /**
     * type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult) query;
     */
    class QueryArchiveFn(
        methodName: String,
        canister: ICPPrincipal
    ) : ICPQuery (
        methodName = methodName,
        canister = canister
    ) {
        suspend operator fun invoke(args: List<Any>): QueryArchiveResult {
            val result = query(args).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }
    }

    /**
     * type QueryBlocksResponse = record {
     *     // The total number of blocks in the chain.
     *     // If the chain length is positive, the index of the last block is `chain_len - 1`.
     *     chain_length : nat64;
     *
     *     // System certificate for the hash of the latest block in the chain.
     *     // Only present if `query_blocks` is called in a non-replicated query context.
     *     certificate : opt blob;
     *
     *     // List of blocks that were available in the ledger when it processed the call.
     *     //
     *     // The blocks form a contiguous range, with the first block having index
     *     // [first_block_index] (see below), and the last block having index
     *     // [first_block_index] + len(blocks) - 1.
     *     //
     *     // The block range can be an arbitrary sub-range of the originally requested range.
     *     blocks : vec Block;
     *
     *     // The index of the first block in "blocks".
     *     // If the blocks vector is empty, the exact value of this field is not specified.
     *     first_block_index : BlockIndex;
     *
     *     // Encoding of instructions for fetching archived blocks whose indices fall into the
     *     // requested range.
     *     //
     *     // For each entry `e` in [archived_blocks], `[e.from, e.from + len)` is a sub-range
     *     // of the originally requested block range.
     *     archived_blocks : vec record {
     *         // The index of the first archived block that can be fetched using the callback.
     *         start : BlockIndex;
     *
     *         // The number of blocks that can be fetched using the callback.
     *         length : nat64;
     *
     *         // The function that should be called to fetch the archived blocks.
     *         // The range of the blocks accessible using this function is given by [from]
     *         // and [len] fields above.
     *         callback : QueryArchiveFn;
     *     };
     * };
     */
    data class QueryBlocksResponse (
        // System certificate for the hash of the latest block in the chain.
        // Only present if `query_blocks` is called in a non-replicated query context.
        val chain_length: ULong,
        // List of blocks that were available in the ledger when it processed the call.
        //
        // The blocks form a contiguous range, with the first block having index
        // [first_block_index] (see below), and the last block having index
        // [first_block_index] + len(blocks) - 1.
        //
        // The block range can be an arbitrary sub-range of the originally requested range.
        val certificate: ByteArray?,
        // The index of the first block in "blocks".
        // If the blocks vector is empty, the exact value of this field is not specified.
        val blocks: Array<Block>,
        // Encoding of instructions for fetching archived blocks whose indices fall into the
        // requested range.
        //
        // For each entry `e` in [archived_blocks], `[e.from, e.from + len)` is a sub-range
        // of the originally requested block range.
        val first_block_index: BlockIndex,
        val archived_blocks: Array<ArchivedBlocks>
    ) {
        data class ArchivedBlocks (
            // The number of blocks that can be fetched using the callback.
            val start: BlockIndex,
            // The function that should be called to fetch the archived blocks.
            // The range of the blocks accessible using this function is given by [from]
            // and [len] fields above.
            val length: ULong,
            val callback: QueryArchiveFn
        )
    }


    /**
     * type Archive = record {
     *     canister_id: principal;
     * };
     */
    data class Archive (
        val canister_id: ICPPrincipal
    )

    /**
     * type Archives = record {
     *     archives: vec Archive;
     * };
     */
    data class Archives (
        val archives: Array<Archive>
    )

    /**
     * type AccountBalanceArgs = record {
     *     account: AccountIdentifier;
     * };
     */
    data class AccountBalanceArgs (
        val account: AccountIdentifier
    )

    class LedgerCanisterService(
        private val canister: ICPPrincipal
    ) {
        // Queries blocks in the specified range.
        suspend fun query_blocks(
            getBlocksArgs: GetBlocksArgs
        ): QueryBlocksResponse {
            val icpQuery = ICPQuery(
                methodName = "query_blocks",
                canister = canister,
            )
            val result = icpQuery.query(listOf(getBlocksArgs)).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }

        // Returns the existing archive canisters information.
        suspend fun archives(): Archives {
            val icpQuery = ICPQuery(
                methodName = "archives",
                canister = canister,
            )
            val result = icpQuery.query(listOf()).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }

        // Get the amount of ICP on the specified account.
        suspend fun account_balance(
            accountBalanceArgs: AccountBalanceArgs
        ): Tokens {
            val icpQuery = ICPQuery(
                methodName = "account_balance",
                canister = canister,
            )
            val result = icpQuery.query(listOf(accountBalanceArgs)).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }

        suspend fun transfer(
            transferArgs: TransferArgs
        ): TransferResult {
            val icpQuery = ICPQuery(
                methodName = "transfer",
                canister = canister,
            )
            val result = icpQuery.query(listOf(transferArgs)).getOrThrow()
            return CandidDecoder.decodeNotNull(result)
        }

    }

}

class Tmp {
    @Test
    @OptIn(ExperimentalStdlibApi::class)
    internal fun test() = runTest {
        val service = LedgerCanister.LedgerCanisterService(
            canister = ICPSystemCanisters.Ledger.icpPrincipal
        )
        val publicKey = "046acf4c93dd993cd736420302eb70da254532ec3179250a21eec4ce823ff289aaa382cb19576b2c6447db09cb45926ebd69ce288b1804580fe62c343d3252ec6e"
        val icpPrincipal = ICPPrincipal.selfAuthenticatingPrincipal(publicKey.hexToByteArray())
        val icpAccount = ICPAccount.mainAccount(icpPrincipal)
        val balance = service.account_balance(
            accountBalanceArgs = LedgerCanister.AccountBalanceArgs(
                account = icpAccount.accountId
            )
        )
        println("Balance for ${icpAccount.address}")
        println(balance)
        println(balance.e8s.toFloat() / 100000000)

        println("Querying blocks #100000")
        val response = service.query_blocks(
            getBlocksArgs = LedgerCanister.GetBlocksArgs(
                start = 100000UL,
                length = 10UL
            )
        )

        val archivedBlocks = response.archived_blocks.first()
        val archivedResponse = archivedBlocks.callback(
            args = listOf(
                LedgerCanister.GetBlocksArgs(
                    start = archivedBlocks.start,
                    length = archivedBlocks.length
                )
            )
        )
        println(
            """
                ********** Archived Response **********
                $archivedResponse
            """.trimIndent()
        )
    }
}

