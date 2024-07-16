package com.bity.icp_kotlin_kit.domain.usecase

import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository

class ICPCanisterUseCase(
    private val icpCanisterRepository: ICPCanisterRepository
) {

    suspend fun query(
        certification: ICPRequestCertification,
        method: ICPMethod,
        canister: ICPPrincipal,
        // TODO sender:
    ): CandidValue =
        when(certification) {
            ICPRequestCertification.Uncertified -> TODO()
            ICPRequestCertification.Certified -> TODO()
        }
}