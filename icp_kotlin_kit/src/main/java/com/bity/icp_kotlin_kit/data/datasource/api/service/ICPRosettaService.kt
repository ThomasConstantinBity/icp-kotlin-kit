package com.bity.icp_kotlin_kit.data.datasource.api.service

import com.bity.icp_kotlin_kit.data.datasource.api.request.RosettaSearchTransactionRequest
import com.bity.icp_kotlin_kit.data.datasource.api.response.RosettaSearchTransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ICPRosettaService {

    @POST("/search/transactions")
    suspend fun searchTransactions(
        @Body requestBody: RosettaSearchTransactionRequest
    ): Response<RosettaSearchTransactionResponse>

}