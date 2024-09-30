package com.bity.icp_kotlin_kit.domain.usecase

import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPTokenBalance
import com.bity.icp_kotlin_kit.domain.repository.TokenRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal class GetBalanceUseCase(
    private val tokenRepository: TokenRepository
) {

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
        }.map {
            ICPTokenBalance(
                token = it.first,
                balance = it.second
            )
        }
    }
}