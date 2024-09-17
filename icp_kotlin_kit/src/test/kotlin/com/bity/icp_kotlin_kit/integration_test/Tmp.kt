package com.bity.icp_kotlin_kit.integration_test

// TODO, add package name

import com.bity.icp_kotlin_kit.candid.CandidDecoder
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.domain.request.PollingValues
import com.bity.icp_kotlin_kit.domain.usecase.ICPQuery
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

typealias AccountIdentifier = ByteArray
typealias Memo = ULong
typealias SubAccount = ByteArray
typealias Hash = ByteArray
typealias BlockIndex = ULong
typealias Ledger = Array<LedgerCanister.Block>


object LedgerCanister {
    class Tokens(
        val e8s: ULong
    )
    sealed class Operation {
        class Mint(
            val to: AccountIdentifier,
            val amount: Tokens
        ): Operation()
        class Burn(
            val from: AccountIdentifier,
            val amount: Tokens
        ): Operation()
        class Transfer(
            val from: AccountIdentifier,
            val to: AccountIdentifier,
            val amount: Tokens,
            val fee: Tokens
        ): Operation()
    }
    class TimeStamp(
        val timestamp_nanos: ULong
    )
    class Transaction(
        val operation: Operation?,
        val memo: Memo,
        val created_at_time: TimeStamp
    )
    class Block(
        val parent_hash: Hash?,
        val transaction: Transaction,
        val timestamp: TimeStamp
    )
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
    sealed class TransferError {
        class BadFee(
            val expected_fee: Tokens
        ): TransferError()
        class InsufficientFunds(
            val balance: Tokens
        ): TransferError()
        class TxTooOld(
            val allowed_window_nanos: ULong
        ): TransferError()
        data object TxCreatedInFuture : TransferError()
        class TxDuplicate(
            val duplicate_of: BlockIndex
        ): TransferError()
    }
    sealed class TransferResult {
        class Ok(
            val blockIndex: BlockIndex
        ): TransferResult()
        class Err(
            val transferError: TransferError
        ): TransferResult()
    }
    class GetBlocksArgs(
        // The index of the first block to fetch.
        val start: BlockIndex,
        // Max number of blocks to fetch.
        val length: ULong
    )
    class BlockRange(
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
    sealed class QueryArchiveResult {
        class Ok(
            val blockRange: BlockRange
        ): QueryArchiveResult()
        data object Err : QueryArchiveResult()
    }
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
    class QueryBlocksResponse(
        // The total number of blocks in the chain.
        // If the chain length is positive, the index of the last block is `chain_len - 1`.
        val chain_length: ULong,
        // System certificate for the hash of the latest block in the chain.
        // Only present if `query_blocks` is called in a non-replicated query context.
        val certificate: ByteArray?,
        // List of blocks that were available in the ledger when it processed the call.
        //
        // The blocks form a contiguous range, with the first block having index
        // [first_block_index] (see below), and the last block having index
        // [first_block_index] + len(blocks) - 1.
        //
        // The block range can be an arbitrary sub-range of the originally requested range.
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

    class Archive(
        val canister_id: ICPPrincipal
    )
    class Archives(
        val archives: Array<Archive>
    )
    class AccountBalanceArgs(
        val account: AccountIdentifier
    )
    class LedgerCanisterService(
        private val canister: ICPPrincipal
    ) {

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

