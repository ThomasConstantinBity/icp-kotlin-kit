package com.bity.icp_kotlin_kit.domain.usecase

import com.bity.icp_candid.domain.model.CandidDictionary
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_cryptography.ICPCryptography
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.RosettaTransaction
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.domain.model.error.ICPLedgerCanisterError
import com.bity.icp_kotlin_kit.domain.model.error.TransferError
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.bity.icp_kotlin_kit.domain.repository.ICPRosettaRepository
import com.bity.icp_kotlin_kit.domain.request.TransferRequest
import com.bity.icp_kotlin_kit.domain.request.toDataModel
import com.bity.icp_kotlin_kit.util.ext_function.ICPAmount

class ICPLedgerCanisterUseCase(
    private val icpCanisterRepository: ICPCanisterRepository,
    private val rosettaRepository: ICPRosettaRepository
) {

    suspend fun accountBalance(
        account: ICPAccount,
        certification: ICPRequestCertification = ICPRequestCertification.Certified
    ): Result<ULong> {
        val method = accountBalanceMethod(account)
        val result = when(certification) {
            ICPRequestCertification.Uncertified -> icpCanisterRepository.query(method)
            ICPRequestCertification.Certified -> TODO()
        }
        val accountBalance = result.getOrElse {
            return Result.failure(it)
        }.ICPAmount ?: return Result.failure(ICPLedgerCanisterError.InvalidResponse())
        return Result.success(accountBalance)
    }

    suspend fun accountTransactions(address: String): Result<List<RosettaTransaction>> =
        rosettaRepository.accountTransactions(address)

    /**
     * @return the block index of the transaction
     * @see [queryBlock] to fetch the block with the new transaction
     */
    suspend fun transfer(request: TransferRequest): Result<ULong> {
        require(ICPCryptography.isValidAccountId(request.receivingAddress)) {
            throw ICPLedgerCanisterError.InvalidReceivingAddress()
        }
        val method = request.toDataModel()
        val requestId = icpCanisterRepository.callAndPoll(
            method = method,
            sender = request.signingPrincipal
        ).getOrElse { return Result.failure(it) }

        val response = icpCanisterRepository.pollRequestStatus(
            requestId = requestId,
            canister = method.canister,
            sender = request.signingPrincipal,
            durationSeconds = request.durationSeconds,
            waitDurationSeconds = request.waitDurationSeconds
        ).getOrElse { return Result.failure(it) }
        return parseTransferResponse(response)
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

    private fun parseTransferResponse(response: CandidValue): Result<ULong> {
        val variant = response.variantValue
            ?: return Result.failure(ICPLedgerCanisterError.InvalidResponse())
        val blockIndex = variant["Ok"]?.natural64Value

        return if(blockIndex == null) {
            val error = variant["Err"]?.variantValue
                ?: return Result.failure(ICPLedgerCanisterError.InvalidResponse())

            error["BadFee"]?.recordValue?.let { badFee ->
                badFee["expected_fee"]?.ICPAmount?.let { expectedFee ->
                    return Result.failure(TransferError.BadFee(expectedFee))
                }
            }

            error["InsufficientFunds"]?.recordValue?.let { insufficientFunds ->
                insufficientFunds["balance"]?.ICPAmount?.let { balance ->
                    return Result.failure(TransferError.InsufficientFunds(balance))
                }
            }

            error["TxTooOld"]?.recordValue?.let { txTooOld ->
                txTooOld["allowed_window_nanos"]?.natural64Value?.let { allowed ->
                    return Result.failure(TransferError.TransactionTooOld(allowed))
                }
            }

            error["TxCreatedInFuture"]?.let {
                    return Result.failure(TransferError.TransactionCreatedInFuture())
            }

            error["TxDuplicate"]?.recordValue?.let { txDuplicate ->
                txDuplicate["duplicate_of"]?.natural64Value?.let { blockIndex ->
                    return Result.failure(TransferError.TransactionDuplicate(blockIndex))
                }
            }
                        Result.failure(ICPLedgerCanisterError.InvalidResponse())
        } else Result.success(blockIndex)
    }
}