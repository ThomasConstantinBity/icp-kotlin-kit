package com.bity.icp_kotlin_kit.domain.request

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidOption
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.util.DEFAULT_TRANSACTION_FEE
import com.bity.icp_kotlin_kit.util.ext_function.icpAmount
import com.bity.icp_kotlin_kit.util.icpTimestampNow

class TransferRequest(
    val sendingAccount: ICPAccount,
    val receivingAddress: String,
    val amount: ULong,
    val signingPrincipal: ICPSigningPrincipal,
    val fee: ULong = DEFAULT_TRANSACTION_FEE,
    val memo: ULong? = 0UL,
    val pollingValues: PollingValues = PollingValues()
)

@OptIn(ExperimentalStdlibApi::class)
internal fun TransferRequest.toDataModel(): ICPMethod =
    ICPMethod(
        canister = ICPSystemCanisters.Ledger.icpPrincipal,
        methodName = "transfer",
        args = listOf(
            CandidValue.Record(
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
                        "memo" to CandidValue.Natural64(memo ?: 0UL),
                        "created_at_time" to icpTimestampNow()
                    )
                )
            )
        )
    )