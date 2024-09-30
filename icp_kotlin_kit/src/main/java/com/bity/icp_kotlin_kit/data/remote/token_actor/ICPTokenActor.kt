package com.bity.icp_kotlin_kit.data.remote.token_actor

import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import java.math.BigInteger

internal interface ICPTokenActor {
    suspend fun getBalance(principal: ICPPrincipal): BigInteger
}