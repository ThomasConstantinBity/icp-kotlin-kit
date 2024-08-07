package com.bity.icp_kotlin_kit.util.ext_function

import com.bity.icp_kotlin_kit.candid.model.CandidValue

internal val CandidValue.ICPAmount: ULong?
    get() = recordValue?.get("e8s")?.natural64Value

internal val CandidValue.ICPTimestamp: Long?
    get() =
        recordValue
            ?.get("timestamp_nanos")
            ?.natural64Value
            ?.toLong()