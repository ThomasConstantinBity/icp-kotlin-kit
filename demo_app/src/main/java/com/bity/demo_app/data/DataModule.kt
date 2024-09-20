package com.bity.demo_app.data

import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.domain.usecase.ICRC1
import com.bity.icp_kotlin_kit.domain.usecase.LedgerCanister
import com.bity.icp_kotlin_kit.domain.usecase.Tokens
import org.koin.dsl.module

val dataModule = module {

    single {
        LedgerCanister.LedgerCanisterService(
            canister = ICPSystemCanisters.Ledger.icpPrincipal
        )
    }

    single {
        Tokens.TokensService(
            canister = ICPSystemCanisters.TokenRegistry.icpPrincipal
        )
    }
}