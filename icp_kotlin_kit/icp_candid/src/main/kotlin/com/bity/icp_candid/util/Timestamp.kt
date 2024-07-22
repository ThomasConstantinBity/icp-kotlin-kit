package com.bity.icp_candid.util

import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_candid.util.ext_function.icpTimestamp
import java.time.Instant

fun icpTimestampNow(): CandidValue =
    Instant.now()
        .epochSecond.
        toULong()
        .times(1_000_000_000UL)
        .icpTimestamp()