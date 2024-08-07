package com.bity.icp_kotlin_kit.domain.request

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters

class QueryBlockRequest(
    val index: ULong,
    val certification: ICPRequestCertification = ICPRequestCertification.Certified,
    val pollingValues: PollingValues = PollingValues()
)

internal fun QueryBlockRequest.toDataModel(): ICPMethod =
    ICPMethod(
        canister = ICPSystemCanisters.Ledger.icpPrincipal,
        methodName = "query_blocks",
        args = CandidValue.Record(
            CandidDictionary(
                hashMapOf(
                    "start" to CandidValue.Natural64(index),
                    "length" to CandidValue.Natural64(1U)
                )
            )
        )
    )