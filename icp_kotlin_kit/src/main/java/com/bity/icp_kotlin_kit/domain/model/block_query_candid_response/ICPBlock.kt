package com.bity.icp_kotlin_kit.domain.model.block_query_candid_response

import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_candid.util.ext_function.ICPTimestamp
import com.bity.icp_kotlin_kit.domain.model.ICPTransaction
import com.bity.icp_kotlin_kit.domain.model.ICPTransactionType
import com.bity.icp_kotlin_kit.domain.model.error.ICPLedgerCanisterError
import java.math.BigInteger

class ICPBlock internal constructor(
    candidValue: CandidValue
) {
    val parentHash: ByteArray
    val timestamp: Long
    val transaction: ICPBlockTransaction

    init {
        val blockRecord = candidValue.recordValue
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        parentHash = blockRecord["parent_hash"]?.optionValue?.value?.blobValue
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        val transactionValue = blockRecord["transaction"]
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        timestamp = blockRecord["timestamp"]?.ICPTimestamp
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        transaction = ICPBlockTransaction(transactionValue)
    }

    fun getICPTransaction(blockIndex: UInt, hash: ByteArray) =
        ICPTransaction(
            type = ICPTransactionType.init(transaction.operation),
            amount = BigInteger.valueOf(transaction.operation.amount.toLong()),
            fee = transaction.operation.fee?.let { BigInteger.valueOf(it.toLong()) },
            hash = hash,
            blockIndex = BigInteger.valueOf(blockIndex.toLong()),
            memo = transaction.memo,
            createdNanos = transaction.createdNanos
        )
}