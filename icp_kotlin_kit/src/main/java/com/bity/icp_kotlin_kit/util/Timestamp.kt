package com.bity.icp_kotlin_kit.util

import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.util.ext_function.icpTimestamp
import java.time.Instant

internal fun icpTimestampNow(): CandidValue =
    Instant.now()
        .epochSecond
        .toULong()
        .times(1_000_000_000UL)
        .icpTimestamp()