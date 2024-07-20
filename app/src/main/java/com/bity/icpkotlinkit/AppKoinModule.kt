package com.bity.icpkotlinkit

import com.bity.icpkotlinkit.presentation.nav.NavManager
import org.koin.dsl.module

val appModule = module {
    single { NavManager() }
}