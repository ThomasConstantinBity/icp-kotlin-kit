package com.bity.icpkotlinkit.presentation

import com.bity.icp_kotlin_kit.provideICPLedgerCanisterUseCase
import com.bity.icpkotlinkit.presentation.icp_account.ICPAccountViewModel
import com.bity.icpkotlinkit.presentation.nav.NavManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel {
        ICPAccountViewModel(
            icpLedgerCanisterUseCase = provideICPLedgerCanisterUseCase()
        )
    }

    single { NavManager() }
}