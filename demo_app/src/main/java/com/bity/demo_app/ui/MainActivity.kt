package com.bity.demo_app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bity.demo_app.ui.icp_balance.AddressBalance
import com.bity.demo_app.ui.main_screen.MainScreen
import com.bity.demo_app.ui.theme.ICPKotlinKitTheme
import com.bity.demo_app.ui.tokens_balance.TokensBalance
import com.bity.demo_app.ui.util.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ICPKotlinKitTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.MainScreen.route
                ) {
                    composable(route = Screen.MainScreen.route) {
                        MainScreen(navController)
                    }
                    composable(route = Screen.AddressBalance.route) {
                        AddressBalance()
                    }
                    composable(route = Screen.TokensBalance.route) {
                        TokensBalance()
                    }
                }
            }
        }
    }
}