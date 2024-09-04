package com.bity.icp_kotlin_kit.domain.request

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters

class AccountBalanceRequest(
    val account: ICPAccount,
    val certification: ICPRequestCertification = ICPRequestCertification.Certified,
    val pollingValues: PollingValues = PollingValues()
)

internal fun AccountBalanceRequest.toDataModel(): ICPMethod =
    ICPMethod(
        canister = ICPSystemCanisters.Ledger.icpPrincipal,
        methodName = "account_balance",
        args = listOf(
            CandidValue.Record(
                CandidDictionary(
                    hashMapOf(
                        "account" to CandidValue.Blob(account.accountId)
                    )
                )
            )
        )
    )