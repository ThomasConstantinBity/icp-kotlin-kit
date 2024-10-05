package com.bity.icp_kotlin_kit.domain.model.error

import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPTokenStandard

sealed class TokenActorException(error: String? = null): Exception(error) {
    class NullActorException(
        standard: ICPTokenStandard,
        canister: ICPPrincipal
    ): TokenActorException("Missing actor for token[${standard.name}] ${canister.string}")
}