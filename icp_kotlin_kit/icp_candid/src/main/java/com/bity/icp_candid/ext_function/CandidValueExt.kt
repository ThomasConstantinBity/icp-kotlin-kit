package com.bity.icp_candid.ext_function

import domain.model.CandidDictionary
import domain.model.CandidValue
import java.time.Instant

internal val CandidValue.ICPAmount: ULong?
    get() = recordValue?.get("e8s")?.natural64Value

internal val CandidValue.ICPTimestamp: Long?
    get() =
        recordValue
            ?.get("timestamp_nanos")
            ?.natural64Value
            ?.toLong()


internal fun CandidValue.ICPAmount(amount: ULong): CandidValue =
    CandidValue.Record(
        CandidDictionary(
            hashMapOf("e8s" to CandidValue.Natural64(amount))
        )
    )

internal fun CandidValue.ICPTimestamp(timestamp: ULong): CandidValue {
    return CandidValue.Record(
        CandidDictionary(
            hashMapOf(
                "timestamp_nanos" to CandidValue.Natural64(timestamp)
            )
        )
    )
}

internal fun CandidValue.ICPTimestampNow(): CandidValue =
    ICPTimestamp(
        timestamp = Instant.now()
            .epochSecond
            .toULong()
            .times(1_000_000_000UL)
    )