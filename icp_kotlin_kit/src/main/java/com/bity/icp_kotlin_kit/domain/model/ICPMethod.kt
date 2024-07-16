package com.bity.icp_kotlin_kit.domain.model

import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPMethodApiModel

class ICPMethod(
    val canister: ICPPrincipal,
    val methodName: String,
    val args: CandidValue?
)

fun ICPMethod.toDataModel(): ICPMethodApiModel =
    ICPMethodApiModel(
        canister = canister.toDataModel(),
        methodName = methodName,
        args = args
    )