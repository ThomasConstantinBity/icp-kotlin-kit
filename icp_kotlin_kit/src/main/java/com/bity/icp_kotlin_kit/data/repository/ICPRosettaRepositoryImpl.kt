package com.bity.icp_kotlin_kit.data.repository

import com.bity.icp_kotlin_kit.data.datasource.api.request.RosettaSearchTransactionRequest
import com.bity.icp_kotlin_kit.data.datasource.api.request.model.AccountIdentifierApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.request.model.NetworkIdentifierApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.response.model.toDomainModel
import com.bity.icp_kotlin_kit.data.datasource.api.service.ICPRosettaService
import com.bity.icp_kotlin_kit.data.model.RemoteClientError
import com.bity.icp_kotlin_kit.domain.model.RosettaTransaction
import com.bity.icp_kotlin_kit.domain.repository.ICPRosettaRepository

class ICPRosettaRepositoryImpl(
    private val client: ICPRosettaService
): ICPRosettaRepository {

    private val icpNetworkIdentifier: NetworkIdentifierApiModel =
        NetworkIdentifierApiModel(
            blockchain = "Internet Computer",
            network = "00000000000000020101"
        )

    override suspend fun accountTransactions(address: String): Result<List<RosettaTransaction>> {

        val requestBody = RosettaSearchTransactionRequest(
            networkIdentifier = icpNetworkIdentifier,
            accountIdentifier = AccountIdentifierApiModel(
                address = address
            )
        )

        client.searchTransactions(requestBody).apply {
            if(!isSuccessful) {
               return Result.failure(RemoteClientError.HttpError(code(), errorBody()?.string()))
            }

            val body = body() ?: return Result.failure(RemoteClientError.MissingBody())
            return Result.success(body.transactions.map { it.toDomainModel() })
        }
    }

}