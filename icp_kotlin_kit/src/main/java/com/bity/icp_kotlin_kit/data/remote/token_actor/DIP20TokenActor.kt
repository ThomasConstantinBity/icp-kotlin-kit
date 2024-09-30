package com.bity.icp_kotlin_kit.data.remote.token_actor

import com.bity.icp_kotlin_kit.domain.generated_file.DIP20
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import java.math.BigInteger

class DIP20TokenActor(
    private val service: DIP20.DIP20Service
): ICPTokenActor {
    override suspend fun getBalance(principal: ICPPrincipal): BigInteger =
        service.balanceOf(principal)
}