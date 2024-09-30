package com.bity.demo_app.data

import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.domain.generated_file.LedgerCanister
import com.bity.icp_kotlin_kit.domain.usecase.token.GetTokenBalanceUseCase
import org.koin.dsl.module

val dataModule = module {

    single {
        LedgerCanister.LedgerCanisterService(
            canister = ICPSystemCanisters.Ledger.icpPrincipal
        )
    }

    single { GetTokenBalanceUseCase() }
}