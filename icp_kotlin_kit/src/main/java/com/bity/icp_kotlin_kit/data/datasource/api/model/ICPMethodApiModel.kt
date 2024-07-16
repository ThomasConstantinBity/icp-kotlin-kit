package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal

class ICPMethodApiModel(
    val canister: ICPPrincipalApiModel,
    val methodName: String,
    val args: CandidValue?
)