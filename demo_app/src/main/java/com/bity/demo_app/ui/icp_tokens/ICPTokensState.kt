package com.bity.demo_app.ui.icp_tokens

import com.bity.icp_kotlin_kit.domain.generated_file.Tokens

sealed class ICPTokensState {
    data object Loading: ICPTokensState()
    data class ICPTokens(val tokens: Array<Tokens.token>): ICPTokensState()
    data class Error(val errorMessage: String? = null): ICPTokensState()
}