package com.bity.icp_kotlin_kit.domain.request

import com.bity.icp_candid.domain.model.CandidDictionary
import com.bity.icp_candid.domain.model.CandidOption
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_candid.util.ext_function.icpAmount
import com.bity.icp_candid.util.icpTimestampNow
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters

class TransferRequest(
    val sendingAccount: ICPAccount,
    val receivingAddress: String,
    val amount: ULong,
    val signingPrincipal: ICPSigningPrincipal,
    val fee: ULong = 10000U,     // 0.0001 ICP
    val memo: ULong?
)

@OptIn(ExperimentalStdlibApi::class)
fun TransferRequest.toDataModel(): ICPMethod =
    ICPMethod(
        canister = ICPSystemCanisters.Ledger.icpPrincipal,
        methodName = "transfer",
        args = CandidValue.Record(
            CandidDictionary(
                hashMapOf(
                    "from_subaccount" to CandidValue.Option(
                        CandidOption.Some(
                            CandidValue.Blob(sendingAccount.subAccountId)
                        )
                    ),
                    "to" to CandidValue.Blob(receivingAddress.hexToByteArray()),
                    "amount" to amount.icpAmount(),
                    "fee" to fee.icpAmount(),
                    "memo" to CandidValue.Natural64(memo ?: 0U),
                    "created_at_time" to icpTimestampNow()
                )
            )
        )
    )