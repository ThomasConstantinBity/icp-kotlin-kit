package com.bity.icp_kotlin_kit.data.repository

import com.bity.icp_candid.domain.deserializer.CandidDeserializer
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPRequestApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.request.ICPRequest
import com.bity.icp_kotlin_kit.data.datasource.api.service.ICPRetrofitService
import com.bity.icp_kotlin_kit.data.model.DataError
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.toDataModel
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.cbor.CBORFactory

internal class ICPCanisterRepositoryImpl(
    private val icpRetrofitService: ICPRetrofitService
): ICPCanisterRepository {

    private val cborFactory = CBORFactory()
    private val objectMapper = ObjectMapper(cborFactory).apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override suspend fun query(method: ICPMethod): CandidValue {
        val request = ICPRequest.init(
            requestType = ICPRequestApiModel.Query(
                icpMethod = method.toDataModel()
            ),
            canister = method.canister.toDataModel()
        )
        val cborEncodedResponse = fetchCbor(request).getOrThrow() ?: TODO() // throw ICPRemoteClientError.NoResponseData
        return parseQueryResponse(cborEncodedResponse)
    }

    private suspend fun fetchCbor(
        request: ICPRequest
    ): Result<ByteArray?> {
        val response = icpRetrofitService.fetch(
            urlPath = request.urlPath,
            body = request.envelope
        )
        return if(response.isSuccessful)
            Result.success(response.body()?.bytes())
        else Result.failure(
            DataError.HttpError(
                errorCode = response.code(),
                errorMessage = response.errorBody().toString()
            )
        )
    }

    private fun parseQueryResponse(data: ByteArray): CandidValue {
        val queryResponse = objectMapper.readValue(data, QueryResponseDecodable::class.java)
        require(queryResponse.status != ICPRequestStatusCode.Rejected) {
            /* throw ICPRemoteClientError.RequestRejected(
                rejectCode = queryResponse.rejectCode,
                rejectMessage = queryResponse.rejectMessage,
                errorCode = queryResponse.errorCode
            ) */
        }
        val candidRaw = queryResponse.reply?.arg ?: TODO() // throw ICPRemoteClientError.MalformedResponse
        val candidResponse = CandidDeserializer.decode(candidRaw)
        return candidResponse.firstOrNull() ?: TODO() // throw ICPRemoteClientError.MalformedResponse
    }

}

class QueryResponseDecodable @JsonCreator constructor(
    @JsonProperty("status") val status: ICPRequestStatusCode,
    @JsonProperty("reply") val reply: Reply?,
    @JsonProperty("reject_code") val rejectCode: ICPRequestRejectCode? = null,
    @JsonProperty("reject_message") val rejectMessage: String? = null,
    @JsonProperty("error_code") val errorCode: String? = null
) {
    class Reply (
        @JsonProperty("arg") val arg: ByteArray
    )
}

enum class ICPRequestStatusCode {
    @JsonProperty(value = "received") Received,
    @JsonProperty(value = "processing") Processing,
    @JsonProperty(value = "replied") Replied,
    @JsonProperty(value = "rejected") Rejected,
    @JsonProperty(value = "done") Done;

    companion object {
        fun valueFromString(string: String): ICPRequestStatusCode =
            when(string) {
                "received" -> Received
                "processing" -> Processing
                "replied" -> Replied
                "rejected" -> Rejected
                "done" -> Done
                else ->throw Error("No value for $string")
            }
    }
}

// @JsonDeserialize(using = ICPRequestRejectCodeDeserializer::class)
enum class ICPRequestRejectCode(val errorCode: Int) {
    SystemFatal(1),
    SystemTransient(2),
    DestinationInvalid(3),
    CanisterReject(4),
    CanisterError(5);

    companion object {
        fun valueFromErrorCode(errorCode: Int): ICPRequestRejectCode =
            when(errorCode) {
                1 -> SystemFatal
                2 -> SystemTransient
                3 -> DestinationInvalid
                4 -> CanisterReject
                5 -> CanisterError
                else -> throw Error("No value for $errorCode")
            }
    }
}