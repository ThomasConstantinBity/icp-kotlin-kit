package com.bity.demo_app.ui.icp_tokens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_kotlin_kit.domain.usecase.Tokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ICPTokensViewModel(
    private val tokensService: Tokens.TokensService
): ViewModel() {

    var state: ICPTokensState by mutableStateOf(ICPTokensState.ICPTokens(emptyArray()))
        private set

    init {
        state = ICPTokensState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            state = try {
                val tokens = tokensService.get_all()
                ICPTokensState.ICPTokens(tokens)
            } catch (ex: Exception) {
                ICPTokensState.Error(ex.message)
            }
        }
    }
}