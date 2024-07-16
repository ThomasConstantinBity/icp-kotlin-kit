package com.bity.icp_kotlin_kit.data.datasource.api.service

import com.bity.icp_kotlin_kit.data.datasource.api.request.ICPRequestEnvelope
import com.bity.icp_kotlin_kit.data.datasource.api.response.QueryResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

internal interface ICPRetrofitService {
    @POST("{urlPath}")
    @Headers("Content-Type: application/cbor")
    suspend fun query(
        @Path("urlPath", encoded = true) urlPath: String,
        @Body body: ICPRequestEnvelope
    ): Response<QueryResponse>
}