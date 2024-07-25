package com.bity.icp_kotlin_kit.domain.usecase

import com.bity.icp_candid.domain.model.CandidDictionary
import com.bity.icp_candid.domain.model.CandidFunction
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_cryptography.ICPCryptography
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPTransaction
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.model.error.ICPLedgerCanisterError
import com.bity.icp_kotlin_kit.domain.model.error.TransferError
import com.bity.icp_kotlin_kit.domain.model.block_query_candid_response.BlockQueryCandidResponse
import com.bity.icp_kotlin_kit.domain.model.block_query_candid_response.ICPBlock
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.bity.icp_kotlin_kit.domain.repository.ICPRosettaRepository
import com.bity.icp_kotlin_kit.domain.request.AccountBalanceRequest
import com.bity.icp_kotlin_kit.domain.request.AccountTransactionRequest
import com.bity.icp_kotlin_kit.domain.request.PollingValues
import com.bity.icp_kotlin_kit.domain.request.QueryBlockRequest
import com.bity.icp_kotlin_kit.domain.request.TransferRequest
import com.bity.icp_kotlin_kit.domain.request.toDataModel
import com.bity.icp_kotlin_kit.util.ext_function.ICPAmount

class ICPLedgerCanisterUseCase(
    private val icpCanisterRepository: ICPCanisterRepository,
    private val rosettaRepository: ICPRosettaRepository
) {

    private suspend fun query(
        method: ICPMethod,
        certification: ICPRequestCertification,
        sender: ICPSigningPrincipal? = null,
        pollingValues: PollingValues
    ): Result<CandidValue> =
        when(certification) {
            ICPRequestCertification.Uncertified -> icpCanisterRepository.query(method)
            ICPRequestCertification.Certified -> {
                val requestId = icpCanisterRepository.call(
                    method = method,
                    sender = sender
                ).getOrElse { return Result.failure(it) }
                icpCanisterRepository.pollRequestStatus(
                    requestId = requestId,
                    canister = method.canister,
                    sender = sender,
                    durationSeconds = pollingValues.durationSeconds,
                    waitDurationSeconds = pollingValues.waitDurationSeconds
                )
            }
        }

    suspend fun accountBalance(request: AccountBalanceRequest): Result<ULong> {
        val method = request.toDataModel()
        val result = query(
            method = method,
            certification = request.certification,
            pollingValues = request.pollingValues
        )
        val accountBalance = result
            .getOrElse { return Result.failure(it) }
            .ICPAmount ?: return Result.failure(ICPLedgerCanisterError.InvalidResponse())
        return Result.success(accountBalance)
    }

    /**
     * @return the block index of the transaction
     * @see [queryBlock] to fetch the block with the new transaction
     */
    suspend fun transfer(request: TransferRequest): Result<ULong> {
        require(ICPCryptography.isValidAccountId(request.receivingAddress)) {
            throw ICPLedgerCanisterError.InvalidReceivingAddress()
        }
        val method = request.toDataModel()
        val response = query(
            method = method,
            certification = ICPRequestCertification.Certified,
            sender = request.signingPrincipal,
            pollingValues = request.pollingValues
        ).getOrElse { return Result.failure(it) }
        return parseTransferResponse(response)
    }

    // TODO: improve using asyncMap
    suspend fun accountTransactions(request: AccountTransactionRequest): Result<List<ICPTransaction>> {
        val rosettaResponse = rosettaRepository.accountTransactions(request.address)
        return when (request.certification) {
            ICPRequestCertification.Uncertified -> rosettaResponse
            ICPRequestCertification.Certified -> {
                val transactions = rosettaResponse.getOrElse { return Result.failure(it) }
                    .map { transaction ->
                        println("Querying block with index ${transaction.blockIndex}")
                        val block = queryBlock(
                            request = QueryBlockRequest(
                                certification = request.certification,
                                index = transaction.blockIndex.toLong().toULong(),
                                pollingValues = request.pollingValues
                            )
                        ).getOrElse { return Result.failure(it) }
                        block.getICPTransaction(
                            blockIndex = transaction.blockIndex.toLong().toUInt(),
                            hash = transaction.hash
                        )
                    }
                Result.success(transactions)
            }
        }
    }

    suspend fun queryBlock(request: QueryBlockRequest): Result<ICPBlock> {
        val method = request.toDataModel()
        val response = query(
            method = method,
            certification = request.certification,
            pollingValues = request.pollingValues
        ).getOrElse { return Result.failure(it) }

        val queryBlockResponse = try {
            BlockQueryCandidResponse(response)
        } catch (err: ICPLedgerCanisterError) {
            return Result.failure(err)
        }

        return when {
            queryBlockResponse.blocks.isNotEmpty() ->
                Result.success(queryBlockResponse.blocks.first())

            queryBlockResponse.archivedBlocks.isNotEmpty() -> {
                val archivedBlock = queryBlockResponse.archivedBlocks.first()
                val archivedBlockMethod = archivedBlock.callback.method
                    ?: return Result.failure(ICPLedgerCanisterError.InvalidResponse())
                val archivedBlockList = try {
                    queryArchivedBlock(
                        certification = request.certification,
                        method = archivedBlockMethod,
                        pollingValues = request.pollingValues,
                        start = archivedBlock.start,
                        length = archivedBlock.length
                    )
                } catch (err: Error) {
                    return Result.failure(err)
                }

                return if(archivedBlockList.isNotEmpty()) {
                    Result.success(archivedBlockList.first())
                } else {
                    Result.failure(ICPLedgerCanisterError.BlockNotFound())
                }
            }
            else -> Result.failure(ICPLedgerCanisterError.BlockNotFound())
        }
    }

    private suspend fun queryArchivedBlock(
        certification: ICPRequestCertification,
        method: CandidFunction.ServiceMethod,
        pollingValues: PollingValues,
        start: ULong,
        length: ULong,
    ): List<ICPBlock> {
        val queryArchiveMethod = queryArchivedBlockMethod(
            method = method,
            start = start,
            length = length
        )
        val archiveResponse = query(
            method = queryArchiveMethod,
            certification = certification,
            sender = null,
            pollingValues = pollingValues
        ).getOrThrow()

        val archive = archiveResponse.variantValue ?: throw ICPLedgerCanisterError.InvalidResponse()
        val ok = archive["Ok"]?.recordValue ?: throw ICPLedgerCanisterError.InvalidResponse()
        val blocks = ok["blocks"]?.vectorValue?.values ?: throw ICPLedgerCanisterError.InvalidResponse()
        return blocks.map { ICPBlock(it) }
    }

    private fun queryArchivedBlockMethod(
        method: CandidFunction.ServiceMethod,
        start: ULong,
        length: ULong
    ): ICPMethod {
        val archivePrincipal = ICPPrincipal.init(method.principalId)
        return ICPMethod(
            canister = archivePrincipal,
            methodName = method.name,
            args = CandidValue.Record(
                CandidDictionary(
                    hashMapOf(
                        "start" to CandidValue.Natural64(start),
                        "length" to CandidValue.Natural64(length)
                    )
                )
            )
        )
    }

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