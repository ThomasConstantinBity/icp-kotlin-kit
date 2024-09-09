package com.bity.icp_kotlin_kit.domain.usecase

import com.bity.icp_kotlin_kit.candid.CandidEncoder
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.bity.icp_kotlin_kit.domain.request.PollingValues
import com.bity.icp_kotlin_kit.provideICPCanisterRepository

open class ICPQuery(
    private val methodName: String,
    private val canister: ICPPrincipal,
) {
    private val icpCanisterRepository: ICPCanisterRepository = provideICPCanisterRepository()

    internal suspend fun query(
        args: List<Any>?,
        certification: ICPRequestCertification = ICPRequestCertification.Uncertified,
        sender: ICPSigningPrincipal? = null,
        pollingValues: PollingValues = PollingValues()
    ): Result<CandidValue> {
        val icpMethod = ICPMethod(
            canister = canister,
            methodName = methodName,
            args = args?.map { CandidEncoder(it) }
        )
        return when(certification) {
            ICPRequestCertification.Uncertified -> icpCanisterRepository.query(icpMethod)
            ICPRequestCertification.Certified -> {
                val requestId = icpCanisterRepository.call(
                    method = icpMethod,
                    sender = sender
                ).getOrElse { return Result.failure(it) }
                icpCanisterRepository.pollRequestStatus(
                    requestId = requestId,
                    canister = canister,
                    sender = sender,
                    durationSeconds = pollingValues.durationSeconds,
                    waitDurationSeconds = pollingValues.waitDurationSeconds
                )
            }
        }
    }
}