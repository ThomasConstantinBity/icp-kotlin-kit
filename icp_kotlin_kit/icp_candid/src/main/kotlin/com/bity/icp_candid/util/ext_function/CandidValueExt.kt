package com.bity.icp_candid.util.ext_function

import com.bity.icp_candid.domain.model.CandidValue

val CandidValue.ICPAmount: ULong?
    get() = recordValue?.get("e8s")?.natural64Value

val CandidValue.ICPTimestamp: Long?
    get() =
        recordValue
            ?.get("timestamp_nanos")
            ?.natural64Value
            ?.toLong()