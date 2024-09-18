package com.bity.demo_app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bity.demo_app.ui.address_balance.AddressBalance
import com.bity.demo_app.ui.theme.ICPKotlinKitTheme
import com.bity.demo_app.ui.util.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ICPKotlinKitTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.AddressBalance.route
                ) {
                    composable(route = Screen.AddressBalance.route) {
                        AddressBalance()
                    }
                }
            }
        }
    }
}