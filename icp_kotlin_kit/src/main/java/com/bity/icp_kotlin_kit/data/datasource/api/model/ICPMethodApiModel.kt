package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_candid.domain.model.CandidValue

class ICPMethodApiModel(
    val canister: ICPPrincipalApiModel,
    val methodName: String,
    val args: CandidValue?
)