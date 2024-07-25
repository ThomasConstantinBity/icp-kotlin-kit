package com.bity.icp_kotlin_kit.data.datasource.api.response.model

import com.bity.icp_kotlin_kit.data.datasource.api.response.model.enum.RosettaTransactionTypeApi
import com.bity.icp_kotlin_kit.data.model.RemoteClientError
import com.bity.icp_kotlin_kit.domain.model.ICPTransaction
import com.bity.icp_kotlin_kit.domain.model.ICPTransactionType
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

class RosettaTransactionApiModel(
    @JsonProperty(value = "block_identifier") val blockIdentifier: RosettaBlockIdentifierApiModel,
    @JsonProperty(value = "transaction") val transaction: RosettaTransactionDetailsApiModel
)

@OptIn(ExperimentalStdlibApi::class)
internal fun RosettaTransactionApiModel.toDomainModel(): ICPTransaction =
    ICPTransaction(
        type = transactionType(transaction.operations),
        amount = transactionAmount(transaction.operations),
        fee = feeFromOperations(transaction.operations),
        hash = transaction.transactionIdentifier.hash.hexToByteArray(),
        blockIndex = blockIdentifier.index,
        memo = transaction.metadata.memo.toLong().toULong(),
        createdNanos = transaction.metadata.timestamp
    )

private fun transactionAmount(operations: List<RosettaTransactionOperationApiModel>): BigInteger =
    operations.find { it.type == RosettaTransactionTypeApi.Transaction }?.amount?.value?.abs()
        ?: throw RemoteClientError.RosettaParsingError("Unable to parse transaction amount")

private fun feeFromOperations(operations: List<RosettaTransactionOperationApiModel>): BigInteger =
    operations.find { it.type == RosettaTransactionTypeApi.Fee }?.amount?.value?.abs()
        ?: throw RemoteClientError.RosettaParsingError("Unable to parse fee")

private fun transactionType(operations: List<RosettaTransactionOperationApiModel>): ICPTransactionType {

    val from = operations.find { it.amount.value < BigInteger.ZERO }?.account?.address
        ?: throw RemoteClientError.RosettaParsingError("Unable to parse from address")
    val to = operations.find { it.amount.value > BigInteger.ZERO }?.account?.address
        ?: throw RemoteClientError.RosettaParsingError("Unable to parse to address")

    return ICPTransactionType.Send(
        from = from,
        to = to
    )
}