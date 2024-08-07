package com.bity.icp_kotlin_kit.domain.model.block_query_candid_response

import com.bity.icp_kotlin_kit.candid.model.CandidFunction
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.error.ICPLedgerCanisterError

internal class ArchivedBlock (
    candidValue: CandidValue
) {
    val start: ULong
    val length: ULong
    val callback: CandidFunction

    init {
        val archivedBlock = candidValue.recordValue ?: throw ICPLedgerCanisterError.InvalidResponse()
        start = archivedBlock["start"]?.natural64Value ?: throw ICPLedgerCanisterError.InvalidResponse()
        length = archivedBlock["length"]?.natural64Value ?: throw ICPLedgerCanisterError.InvalidResponse()
        callback = archivedBlock["callback"]?.functionValue ?: throw ICPLedgerCanisterError.InvalidResponse()
    }
}