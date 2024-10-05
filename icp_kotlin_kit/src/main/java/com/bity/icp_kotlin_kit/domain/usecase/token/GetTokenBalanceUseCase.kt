package com.bity.icp_kotlin_kit.domain.usecase.token

import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPTokenBalance
import com.bity.icp_kotlin_kit.domain.repository.TokenRepository
import com.bity.icp_kotlin_kit.provideTokenRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.math.BigInteger

class GetTokenBalanceUseCase internal constructor(
    private val tokenRepository: TokenRepository
) {

    constructor(): this(
        tokenRepository = provideTokenRepository()
    )

    suspend operator fun invoke(principal: ICPPrincipal): List<ICPTokenBalance> = coroutineScope {
        val tokens = tokenRepository.getAllTokens()
        tokens.map { token ->
            token to async {
                tokenRepository.getTokenBalance(
                    standard = token.standard,
                    canister = token.canister,
                    principal = principal
                )
            }
        }.mapNotNull {
            val value = it.second.await() ?: return@mapNotNull null
            it.first to value
        }.filter {
            it.second != BigInteger.ZERO
        }.map {
            ICPTokenBalance(
                token = it.first,
                balance = it.second
            )
        }
    }
}