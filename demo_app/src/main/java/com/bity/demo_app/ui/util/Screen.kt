package com.bity.demo_app.ui.util

sealed class Screen(val route: String) {
    object AddressBalance: Screen("address_balance")
}