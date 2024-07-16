package com.bity.icp_kotlin_kit.data.repository

import retrofit2.Converter
import retrofit2.Retrofit
import okhttp3.ResponseBody
import okhttp3.RequestBody
import java.lang.reflect.Type
import com.bity.icp_candid.domain.deserializer.CandidDeserializer
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPRequestApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.request.ICPRequest
import com.bity.icp_kotlin_kit.data.datasource.api.service.ICPRetrofitService
import com.bity.icp_kotlin_kit.data.model.DataError
import com.bity.icp_kotlin_kit.data.model.RemoteClientError
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.toDataModel
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory

internal class ICPCanisterRepositoryImpl(
    private val icpRetrofitService: ICPRetrofitService
): ICPCanisterRepository {

    override suspend fun query(method: ICPMethod): Result<CandidValue> {
        val request = ICPRequest.init(
            requestType = ICPRequestApiModel.Query(
                icpMethod = method.toDataModel()
            ),
            canister = method.canister.toDataModel()
        )
        icpRetrofitService.query(
            urlPath = request.urlPath,
            body = request.envelope
        ).apply {
            require(isSuccessful) {
                return Result.failure(
                    RemoteClientError.HttpError(
                        errorCode = code(),
                        errorMessage = errorBody().toString()
                    )
                )
            }
            val responseBody = body() ?: return Result.failure(RemoteClientError.MissingBody())
            val arg = body()?.reply?.arg
            val candidValue = CandidDeserializer.decode(arg!!).firstOrNull()
                ?: return Result.failure(RemoteClientError.ParsingError(arg))
            return Result.success(candidValue)
        }
    }

    private suspend fun fetchCbor(
        request: ICPRequest
    ): Result<ByteArray?> {
        val response = icpRetrofitService.query(
            urlPath = request.urlPath,
            body = request.envelope
        )
        return if(response.isSuccessful)
            TODO()
            // Result.success(response.body()?.bytes())
        else Result.failure(
            DataError.HttpError(
                errorCode = response.code(),
                errorMessage = response.errorBody().toString()
            )
        )
    }
}


