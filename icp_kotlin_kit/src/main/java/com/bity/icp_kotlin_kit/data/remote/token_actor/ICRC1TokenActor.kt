package com.bity.icp_kotlin_kit.data.remote.token_actor

import com.bity.icp_kotlin_kit.domain.generated_file.ICRC1
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import java.math.BigInteger

internal class ICRC1TokenActor(
    private val service: ICRC1.ICRC1Service
): ICPTokenActor {

    override suspend fun getBalance(principal: ICPPrincipal): BigInteger {
        val account = ICRC1.Account(
            owner = principal,
            subaccount = null
        )
        return service.icrc1_balance_of(account)
    }
}