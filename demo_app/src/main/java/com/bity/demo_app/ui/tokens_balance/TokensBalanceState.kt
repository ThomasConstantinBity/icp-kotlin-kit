package com.bity.demo_app.ui.tokens_balance

import com.bity.icp_kotlin_kit.domain.usecase.Tokens
import java.math.BigInteger

sealed class TokensBalanceState {
    data object Loading: TokensBalanceState()
    data class TokenWithBalance(
        val balances: List<TokenWithBalanceModel> = emptyList()
    ): TokensBalanceState()
    data class Error(val error: String?): TokensBalanceState()
}

data class TokenWithBalanceModel(
    val token: Tokens.token,
    val balance: BigInteger
)