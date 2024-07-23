package com.bity.icp_kotlin_kit.domain.repository

import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal

interface ICPCanisterRepository {

    suspend fun query(
        method: ICPMethod,
        sender: ICPSigningPrincipal? = null
    ): Result<CandidValue>

    suspend fun callAndPoll(
        method: ICPMethod,
        sender: ICPSigningPrincipal? = null,
    ): Result<ByteArray>

    suspend fun pollRequestStatus(
        requestId: ByteArray,
        canister: ICPPrincipal,
        sender: ICPSigningPrincipal? = null,
        durationSeconds: Long,
        waitDurationSeconds: Long
    ): Result<CandidValue>
}