package com.bity.icpkotlinkit.presentation

import com.bity.icpkotlinkit.presentation.icp_account.ICPAccountViewModel
import com.bity.icpkotlinkit.presentation.nav.NavManager
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {

    viewModelOf(::ICPAccountViewModel)

    single { NavManager() }
}