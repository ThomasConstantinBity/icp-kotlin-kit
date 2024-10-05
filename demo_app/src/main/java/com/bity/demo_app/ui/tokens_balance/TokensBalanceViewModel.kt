package com.bity.demo_app.ui.tokens_balance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.usecase.token.GetTokenBalanceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TokensBalanceViewModel(
    private val getTokenBalanceUseCase: GetTokenBalanceUseCase
): ViewModel() {

    var state: TokensBalanceState by mutableStateOf(TokensBalanceState.TokenWithBalance())
        private set

    @OptIn(ExperimentalStdlibApi::class)
    fun getTokens(uncompressedPublicKey: String) {
        state = TokensBalanceState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tokens = getTokenBalanceUseCase(
                    ICPPrincipal.selfAuthenticatingPrincipal(uncompressedPublicKey.hexToByteArray())
                )
                state = TokensBalanceState.TokenWithBalance(tokens)
            } catch (ex: Exception) {
                state = TokensBalanceState.Error(ex.message)
            }
        }
    }
}