package com.bity.icp_kotlin_kit.util

internal fun icpTimestampNow(): Long =
    System.currentTimeMillis()
        .times(1_000_000L)