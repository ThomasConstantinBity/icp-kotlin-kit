package com.bity.demo_app.ui

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.bity.demo_app.ui.address_balance.AddressBalanceViewModel

val uiModule = module {

    viewModelOf(::AddressBalanceViewModel)

}