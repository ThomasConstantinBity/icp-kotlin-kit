package com.bity.icp_kotlin_kit.domain.model.block_query_candid_response

import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_candid.util.ext_function.ICPTimestamp
import com.bity.icp_kotlin_kit.domain.model.error.ICPLedgerCanisterError

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
}