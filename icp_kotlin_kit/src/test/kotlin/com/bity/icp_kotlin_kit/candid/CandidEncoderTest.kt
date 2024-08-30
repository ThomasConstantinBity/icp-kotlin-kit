package com.bity.icp_kotlin_kit.candid

import com.bity.icp_kotlin_kit.candid.model.CandidPrimitiveType
import com.bity.icp_kotlin_kit.candid.model.CandidType
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

// TODO, add package name

import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.bity.icp_kotlin_kit.domain.request.PollingValues
import com.bity.icp_kotlin_kit.provideICPCanisterRepository
import com.bity.icp_kotlin_kit.util.ext_function.ICPAmount
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Disabled

/**
 * File generated at 29/08/2024 04:46:29 using ICP Kotlin Kit Plugin
 */

// https://internetcomputer.org/docs/current/references/ledger/

/**
 * type Tokens = record {
 *     e8s : nat64;
 * };
 */
data class Tokens constructor (
    val e8s: ULong
)

/**
 * type AccountIdentifier = blob;
 */
// Account identifier is a 32-byte array.
// The first 4 bytes is big-endian encoding of a CRC32 checksum of the last 28 bytes
typealias AccountIdentifier = ByteArray

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
// There are three types of operations: minting tokens, burning tokens & transferring tokens
sealed class Transfer {
    class Mint(
        val to: AccountIdentifier,
        val amount: Tokens
    ) : Transfer()

    class Burn(
        val from: AccountIdentifier,
        val amount: Tokens
    ) : Transfer()

    class Send(
        val from: AccountIdentifier,
        val to: AccountIdentifier,
        val amount: Tokens
    ) : Transfer()
}

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
 * type TimeStamp = record {
 *     timestamp_nanos: nat64;
 * };
 */
// Timestamps are represented as nanoseconds from the UNIX epoch in UTC timezone
class TimeStamp(
    val timestamp_nanos: ULong
)

/**
 * type Transaction = record {
 *     operation: opt Transfer;
 *     memo: Memo;
 *     created_at_time: TimeStamp;
 * };
 */
class Transaction(
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
class Block(
    val parent_hash: Hash?,
    val transaction: Transaction,
    val timestamp: TimeStamp
)

/**
 * type BlockIndex = nat64;
 */
typealias BlockIndex = ULong

/**
 * type Ledger = vec Block;
 */
// The ledger is a list of blocks
typealias Ledger = Array<Block>

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
// Arguments for the `transfer` call.
class TransferArgs(
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
    // The account specified by the caller doesn't have enough funds.
    class BadFee(
        val expected_fee: Tokens
    ) : TransferError()

    // The request is too old.
    // The ledger only accepts requests created within 24 hours window.
    // This is a non-recoverable error.
    class InsufficientFunds(
        val balance: Tokens
    ) : TransferError()

    // The caller specified `created_at_time` that is too far in future.
    // The caller can retry the request later.
    class TxTooOld(
        val allowed_window_nanos: ULong
    ) : TransferError()

    data object TxCreatedInFuture : TransferError()

    // The ledger has already executed the request.
    // `duplicate_of` field is equal to the index of the block containing the original transaction.
    class TxDuplicate(
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
    class Ok(
        val blockIndex: BlockIndex
    ) : TransferResult()

    class Err(
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
class GetBlocksArgs(
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
// A prefix of the block range specified in the [GetBlocksArgs] request.
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

/**
 * type QueryArchiveResult = variant {
 *     Ok : BlockRange;
 *     Err : null;      // we don't know the values here...
 * };
 */
sealed class QueryArchiveResult {
    class Ok(
        val blockRange: BlockRange
    ) : QueryArchiveResult()

    // we don't know the values here...
    data object Err : QueryArchiveResult()
}

/**
 * type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult) query;
 */
// A function that is used for fetching archived ledger blocks.
typealias QueryArchiveFn = (GetBlocksArgs) -> QueryArchiveResult

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
// The result of a "query_blocks" call.
//
// The structure of the result is somewhat complicated because the main ledger canister might
// not have all the blocks that the caller requested: One or more "archive" canisters might
// store some of the requested blocks.
//
// Note: as of Q4 2021 when this interface is authored, ICP doesn't support making nested
// query calls within a query call.
class QueryBlocksResponse(
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
    class ArchivedBlocks(
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


/**
 * service : {
 *     // Get the amount of ICP on the specified account.
 *     account_balance : (AccountBalanceArgs) -> (Tokens) query;
 * }
 */
class Service private constructor(
    private val canister: ICPPrincipal,
    private val icpCanisterRepository: ICPCanisterRepository
) {
    // Get the amount of ICP on the specified account.
    suspend fun account_balance(
        accountBalanceArgs: AccountBalanceArgs,
        sender: ICPSigningPrincipal? = null,
        certification: ICPRequestCertification = ICPRequestCertification.Uncertified,
        pollingValues: PollingValues = PollingValues()
    ): Tokens {
        val icpMethod = ICPMethod(
            canister = canister,
            methodName = "account_balance",
            args = CandidEncoder(accountBalanceArgs)
        )
        val result = query(
            method = icpMethod,
            certification = certification,
            sender = sender,
            pollingValues = pollingValues
        ).getOrThrow()
        return CandidDecoder(result)
    }

    private suspend fun query(
        method: ICPMethod,
        certification: ICPRequestCertification,
        sender: ICPSigningPrincipal? = null,
        pollingValues: PollingValues
    ): Result<CandidValue> =
        when (certification) {
            ICPRequestCertification.Uncertified -> icpCanisterRepository.query(method)
            ICPRequestCertification.Certified -> {
                val requestId = icpCanisterRepository.call(
                    method = method,
                    sender = sender
                ).getOrElse { return Result.failure(it) }
                icpCanisterRepository.pollRequestStatus(
                    requestId = requestId,
                    canister = method.canister,
                    sender = sender,
                    durationSeconds = pollingValues.durationSeconds,
                    waitDurationSeconds = pollingValues.waitDurationSeconds
                )
            }
        }

    companion object {
        fun init(
            canister: ICPPrincipal
        ): Service =
            Service(
                canister = canister,
                icpCanisterRepository = provideICPCanisterRepository()
            )
    }
}

internal class CandidEncoderTest {

    // @Disabled
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun test() = runTest {
        val icpPrincipal = ICPPrincipal.selfAuthenticatingPrincipal("046acf4c93dd993cd736420302eb70da254532ec3179250a21eec4ce823ff289aaa382cb19576b2c6447db09cb45926ebd69ce288b1804580fe62c343d3252ec6e".hexToByteArray())
        val icpAccount = ICPAccount.mainAccount(icpPrincipal)
        val service = Service.init(canister = ICPSystemCanisters.Ledger.icpPrincipal)
        val balance = service.account_balance(
            accountBalanceArgs = AccountBalanceArgs(icpAccount.accountId)
        )
        println(balance)
    }

    @ParameterizedTest(name = "[{index}] - encoding {0}")
    @MethodSource("unsignedValue")
    fun `encode unsigned value`(
        value: Any,
        expectedResult: CandidValue
    ) {
        assertEquals(
            expectedResult,
            CandidEncoder(value)
        )
    }

    @ParameterizedTest(name = "[{index}] - encoding {0}")
    @MethodSource("signedValue")
    fun `encode signed value`(
        value: Any,
        expectedResult: CandidValue
    ) {
        assertEquals(
            expectedResult,
            CandidEncoder(value)
        )
    }

    @ParameterizedTest(name = "[{index}] - encoding {0}")
    @MethodSource("floatValue")
    fun `encode float value`(
        value: Float,
        expectedResult: CandidValue
    ) {
        assertEquals(
            expectedResult,
            CandidEncoder(value)
        )
    }

    @ParameterizedTest(name = "[{index}] - encoding {0}")
    @MethodSource("doubleValue")
    fun `encode double value`(
        value: Double,
        expectedResult: CandidValue
    ) {
        assertEquals(
            expectedResult,
            CandidEncoder(value)
        )
    }

    @ParameterizedTest(name = "[{index}] - encoding {0}")
    @MethodSource("booleanValue")
    fun `encode boolean value`(
        value: Boolean,
        expectedResult: CandidValue
    ) {
        assertEquals(
            expectedResult,
            CandidEncoder(value)
        )
    }

    @ParameterizedTest(name = "[{index}] - encoding {0}")
    @MethodSource("stringValue")
    fun `encode string value`(
        value: String,
        expectedResult: CandidValue
    ) {
        assertEquals(
            expectedResult,
            CandidEncoder(value)
        )
    }

    @ParameterizedTest(name = "[{index}] - encoding {0}")
    @MethodSource("byteArrayValue")
    fun `encode byteArray value`(
        value: ByteArray,
        expectedResult: CandidValue
    ) {
        assertEquals(
            expectedResult,
            CandidEncoder(value)
        )
    }

    @ParameterizedTest(name = "[{index}] - encoding {0}")
    @MethodSource("nullValue")
    fun `encode null value`(
        expectedClass: Class<*>,
        expectedResult: CandidValue,
        expectedClassNullable: Boolean
    ) {
        assertEquals(
            expectedResult,
            CandidEncoder(
                arg = null,
                expectedClass = expectedClass,
                expectedClassNullable = expectedClassNullable
            )
        )
    }

    companion object {

        @JvmStatic
        private fun unsignedValue() = listOf(

            Arguments.of(
                0.toUByte(),
                CandidValue.Natural8(0U)
            ),

            Arguments.of(
                0.toUShort(),
                CandidValue.Natural16(0U)
            ),

            Arguments.of(
                1.toUShort(),
                CandidValue.Natural16(1U)
            ),

            Arguments.of(
                2.toUInt(),
                CandidValue.Natural32(2U)
            ),

            Arguments.of(
                123.toULong(),
                CandidValue.Natural64(123U)
            )
        )

        @JvmStatic
        private fun signedValue() = listOf(
            Arguments.of(
                (-1).toByte(),
                CandidValue.Integer8(-1)
            ),

            Arguments.of(
                (-5).toShort(),
                CandidValue.Integer16(-5)
            ),

            Arguments.of(
                -100,
                CandidValue.Integer32(-100)
            ),

            Arguments.of(
                (-34567).toLong(),
                CandidValue.Integer64(-34567)
            ),
        )

        @JvmStatic
        private fun floatValue() = listOf(
            Arguments.of(
                1.5.toFloat(),
                CandidValue.Float32(1.5.toFloat())
            )
        )

        @JvmStatic
        private fun doubleValue() = listOf(
            Arguments.of(
                1.5554,
                CandidValue.Float64(1.5554)
            )
        )

        @JvmStatic
        private fun booleanValue() = listOf(
            Arguments.of(
                true,
                CandidValue.Bool(true)
            ),
            Arguments.of(
                false,
                CandidValue.Bool(false)
            )
        )

        @JvmStatic
        private fun stringValue() = listOf(
            Arguments.of(
                "some simple text",
                CandidValue.Text("some simple text")
            )
        )

        @JvmStatic
        private fun byteArrayValue() = listOf(
            Arguments.of(
                byteArrayOf(),
                CandidValue.Blob(byteArrayOf())
            ),
            Arguments.of(
                byteArrayOf(0x00, 0xAA.toByte(), 0xCD.toByte()),
                CandidValue.Blob(byteArrayOf(0x00, 0xAA.toByte(), 0xCD.toByte()))
            )
        )

        @JvmStatic
        private fun nullValue() = listOf(
            Arguments.of(
                Boolean::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.BOOL)
                ),
                false
            ),
            Arguments.of(
                String::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.TEXT)
                ),
                false
            ),
            Arguments.of(
                Long::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.INTEGER64)
                ),
                true
            ),
            Arguments.of(
                UByte::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.NATURAL8)
                ),
                true
            ),
            Arguments.of(
                UShort::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.NATURAL16)
                ),
                true
            ),
            Arguments.of(
                UInt::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.NATURAL32)
                ),
                true
            ),
            Arguments.of(
                ULong::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.NATURAL64)
                ),
                true
            ),
            Arguments.of(
                Byte::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.INTEGER8)
                ),
                true
            ),
            Arguments.of(
                Short::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.INTEGER16)
                ),
                true
            ),
            Arguments.of(
                Int::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.INTEGER32)
                ),
                true
            ),
            Arguments.of(
                Long::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.INTEGER64)
                ),
                true
            ),
            Arguments.of(
                Float::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.FLOAT32)
                ),
                true
            ),
            Arguments.of(
                Double::class.java,
                CandidValue.Option(
                    containedType = CandidType.Primitive(CandidPrimitiveType.FLOAT64)
                ),
                true
            )
        )

        /**
         *     (Double(1.5), .float64(1.5)),
         *
         *     (BigUInt(5), .natural(5)),
         *     (BigInt(-5), .integer(-5)),
         *
         *     (Optional(8), .option(.integer64(8))),
         *     (BigUInt?.none, .option(.natural)),
         *     (BigInt?.none, .option(.integer)),
         *     (Optional(Optional(8)), .option(.option(.integer64(8)))),
         *     (Optional(Int?.none), .option(CandidValue.option(.integer64))),
         */
    }
}