package com.bity.icp_kotlin_kit.domain.request

import com.bity.icp_candid.domain.model.CandidDictionary
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.util.DEFAULT_POLLING_SECONDS_TIMEOUT
import com.bity.icp_kotlin_kit.util.DEFAULT_POLLING_SECONDS_WAIT

class AccountBalanceRequest(
    val account: ICPAccount,
    val certification: ICPRequestCertification = ICPRequestCertification.Certified,
    val pollingValues: PollingValues = PollingValues()
)

fun AccountBalanceRequest.toDataModel(): ICPMethod =
    ICPMethod(
        canister = ICPSystemCanisters.Ledger.icpPrincipal,
        methodName = "account_balance",
        args = CandidValue.Record(
            CandidDictionary(
                hashMapOf(
                    "account" to CandidValue.Blob(account.accountId)
                )
            )
        )
    )