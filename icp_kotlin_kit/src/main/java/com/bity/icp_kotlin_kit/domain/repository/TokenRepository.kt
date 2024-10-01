package com.bity.icp_kotlin_kit.domain.repository

import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPToken
import com.bity.icp_kotlin_kit.domain.model.ICPTokenTransfer
import com.bity.icp_kotlin_kit.domain.model.arg.ICPTokenTransferArgs
import com.bity.icp_kotlin_kit.domain.model.enum.ICPTokenStandard
import java.math.BigInteger

internal interface TokenRepository {
    suspend fun getAllTokens(): List<ICPToken>
    suspend fun getTokenBalance(
        standard: ICPTokenStandard,
        canister: ICPPrincipal,
        principal: ICPPrincipal
    ): BigInteger?
    suspend fun fee(token: ICPToken): BigInteger
    suspend fun send(transferArgs: ICPTokenTransferArgs): ICPTokenTransfer
}