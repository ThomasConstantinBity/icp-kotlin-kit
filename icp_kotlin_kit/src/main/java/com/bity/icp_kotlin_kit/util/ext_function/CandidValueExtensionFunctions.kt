package com.bity.icp_kotlin_kit.util.ext_function

import com.bity.icp_candid.domain.model.CandidValue

val CandidValue.ICPAmount: ULong?
    get() = recordValue?.get("e8s")?.natural64Value