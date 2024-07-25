package com.bity.icp_kotlin_kit.domain.model.block_query_candid_response

import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_candid.util.ext_function.ICPTimestamp
import com.bity.icp_kotlin_kit.domain.model.error.ICPLedgerCanisterError

class ICPBlockTransaction internal constructor(
    candidValue: CandidValue
) {
    val memo: ULong
    val createdNanos: Long
    val operation: ICPBlockOperation

    init {
        val transactionRecord = candidValue.recordValue
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        val operationValue = transactionRecord["operation"]
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        memo = transactionRecord["memo"]?.natural64Value
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        createdNanos = transactionRecord["created_at_time"]?.ICPTimestamp
            ?: throw ICPLedgerCanisterError.InvalidResponse()
        operation = ICPBlockOperation.init(operationValue)
    }
}