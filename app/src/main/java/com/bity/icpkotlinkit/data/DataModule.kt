package com.bity.icpkotlinkit.data

import com.bity.icp_kotlin_kit.domain.usecase.ICPLedgerCanisterUseCase
import org.koin.dsl.module

val dataModule = module {
    single<ICPLedgerCanisterUseCase> {
        ICPLedgerCanisterUseCase.init()
    }
}