package com.bity.icp_kotlin_kit.domain.model

import com.bity.icp_kotlin_kit.domain.model.enum.ICPTokenStandard
import java.math.BigInteger

data class ICPToken(
    val standard: ICPTokenStandard,
    val canister: ICPPrincipal,
    val name: String,
    val decimals: UInt,
    val symbol: String,
    val description: String,
    val totalSupply: BigInteger,
    val verified: Boolean,
    val logoUrl: String?,
    val websiteUrl: String?
)