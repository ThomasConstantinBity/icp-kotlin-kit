package com.bity.icp_kotlin_kit.data.repository

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
            val arg = body()?.reply?.arg ?: return Result.failure(RemoteClientError.MissingBody())
            val candidValue = CandidDeserializer.decode(arg).firstOrNull()
                ?: return Result.failure(RemoteClientError.ParsingError(arg))
            return Result.success(candidValue)
        }
    }
}


