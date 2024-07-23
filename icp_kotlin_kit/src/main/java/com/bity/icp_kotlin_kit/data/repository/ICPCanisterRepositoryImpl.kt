package com.bity.icp_kotlin_kit.data.repository

import com.bity.icp_candid.domain.deserializer.CandidDeserializer
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPRequestApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPStateTreePathApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPStateTreePathComponentApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.request.ICPRequest
import com.bity.icp_kotlin_kit.data.datasource.api.service.ICPRetrofitService
import com.bity.icp_kotlin_kit.data.model.RemoteClientError
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.toDataModel
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import java.util.Date

internal class ICPCanisterRepositoryImpl(
    private val icpRetrofitService: ICPRetrofitService,
): ICPCanisterRepository {

    override suspend fun query(
        method: ICPMethod,
        sender: ICPSigningPrincipal?
    ): Result<CandidValue> {
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

    override suspend fun callAndPoll(
        method: ICPMethod,
        sender: ICPSigningPrincipal?,
        durationSeconds: Long,
        waitDurationSeconds: Long
    ): Result<CandidValue> {
        val requestId = call(
            method = method,
            sender = sender
        ).getOrElse {
            return Result.failure(it)
        }
        val result = pollRequestStatus(
            requestId = requestId,
            canister = method.canister,
            sender = sender,
            waitDurationSeconds = waitDurationSeconds
        )

        TODO()
    }

    /**
     * @return requestId of the request
     * Use [pollRequestStatus] to to get the current status of the request
     */
    private suspend fun call(
        method: ICPMethod,
        sender: ICPSigningPrincipal? = null
    ): Result<ByteArray> {
        val request = ICPRequest.init(
            requestType = ICPRequestApiModel.Call(method.toDataModel()),
            canister = method.canister.toDataModel(),
            sender = sender
        )
        icpRetrofitService.call(
            urlPath = request.urlPath,
            body = request.envelope
        ).apply {
            if(!isSuccessful)
                return Result.failure(
                    RemoteClientError.HttpError(
                        errorCode = code(),
                        errorMessage = errorBody().toString()
                    )
                )
        }
        return Result.success(request.requestId)
    }

    // TODO, make it private
    suspend fun pollRequestStatus(
        requestId: ByteArray,
        canister: ICPPrincipal,
        sender: ICPSigningPrincipal? = null,
        durationSeconds: Long = 120,
        waitDurationSeconds: Long = 2
    ): Result<CandidValue> {
        val endTime = Date(System.currentTimeMillis() + durationSeconds * 1000).time

        val paths: List<ICPStateTreePathApiModel> = listOf(
            listOf(ICPStateTreePathComponentApiModel.StringApiModel("time")),
            listOf(
                ICPStateTreePathComponentApiModel.StringApiModel("request_status"),
                ICPStateTreePathComponentApiModel.DataApiModel(requestId),
                ICPStateTreePathComponentApiModel.StringApiModel("status")
            ),
            listOf(
                ICPStateTreePathComponentApiModel.StringApiModel("request_status"),
                ICPStateTreePathComponentApiModel.DataApiModel(requestId),
                ICPStateTreePathComponentApiModel.StringApiModel("reply")
            ),
            listOf(
                ICPStateTreePathComponentApiModel.StringApiModel("request_status"),
                ICPStateTreePathComponentApiModel.DataApiModel(requestId),
                ICPStateTreePathComponentApiModel.StringApiModel("reject_code")),
            listOf(
                ICPStateTreePathComponentApiModel.StringApiModel("request_status"),
                ICPStateTreePathComponentApiModel.DataApiModel(requestId),
                ICPStateTreePathComponentApiModel.StringApiModel("reject_message")
            )
        ).map { ICPStateTreePathApiModel(it) }

        while (endTime > System.currentTimeMillis()) {
            val status = readState(
                paths = paths,
                canister = canister,
                sender = sender
            )

            println("Looping...")
            TODO()
        }

        TODO()
    }

    private suspend fun readState(
        paths: List<ICPStateTreePathApiModel>,
        canister: ICPPrincipal,
        sender: ICPSigningPrincipal? = null
    ) {
        val request = ICPRequest.init(
            requestType = ICPRequestApiModel.ReadState(paths),
            canister = canister.toDataModel(),
            sender = sender
        )
        icpRetrofitService.readState(
            urlPath = request.urlPath,
            body = request.envelope
        ).apply {
            if(!isSuccessful) {
                TODO()
            }
        }
    }
}


