package com.bity.demo_app.ui.util

sealed class Screen(val route: String) {
    data object MainScreen: Screen("main_screen")
    data object TokensBalance: Screen("tokens_balance")
}