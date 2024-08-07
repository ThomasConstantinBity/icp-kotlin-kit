package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_kotlin_kit.candid.model.CandidValue


internal class ICPMethodApiModel(
    val canister: ICPPrincipalApiModel,
    val methodName: String,
    val args: CandidValue?
)