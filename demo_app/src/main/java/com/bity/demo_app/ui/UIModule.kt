package com.bity.demo_app.ui

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.bity.demo_app.ui.tokens_balance.TokensBalanceViewModel

val uiModule = module {
    viewModelOf(::TokensBalanceViewModel)
}