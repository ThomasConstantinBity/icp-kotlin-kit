package com.bity.demo_app.data

import com.bity.icp_kotlin_kit.domain.usecase.token.GetTokenBalanceUseCase
import org.koin.dsl.module

val dataModule = module {
    single { GetTokenBalanceUseCase() }
}