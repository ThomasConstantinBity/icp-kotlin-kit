package com.bity.icp_kotlin_kit.domain.usecase

import com.bity.icp_candid.domain.model.CandidDictionary
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_cryptography.ICPCryptography
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.domain.model.error.ICPLedgerCanisterError
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.bity.icp_kotlin_kit.domain.request.TransferRequest
import com.bity.icp_kotlin_kit.domain.request.toDataModel
import com.bity.icp_kotlin_kit.util.ext_function.ICPAmount

class ICPLedgerCanisterUseCase(
    private val icpCanisterRepository: ICPCanisterRepository
) {

    suspend fun accountBalance(
        account: ICPAccount,
        certification: ICPRequestCertification = ICPRequestCertification.Certified
    ): ULong {
        val method = accountBalanceMethod(account)
        val result = when(certification) {
            ICPRequestCertification.Uncertified -> icpCanisterRepository.query(method)
            ICPRequestCertification.Certified -> TODO()
        }
        return result.getOrThrow().ICPAmount ?: throw ICPLedgerCanisterError.InvalidResponse()
    }

    suspend fun transfer(request: TransferRequest): Result<ULong> {
        require(ICPCryptography.isValidAccountId(request.receivingAddress)) {
            throw ICPLedgerCanisterError.InvalidReceivingAddress()
        }
        val method = request.toDataModel()
        val result = icpCanisterRepository.callAndPoll(
            method = method,
            sender = request.signingPrincipal
        )
        TODO()
    }

    private fun accountBalanceMethod(account: ICPAccount): ICPMethod =
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
}