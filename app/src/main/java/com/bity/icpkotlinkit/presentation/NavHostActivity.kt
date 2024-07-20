package com.bity.icpkotlinkit.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bity.icpkotlinkit.R
import com.bity.icpkotlinkit.presentation.nav.NavManager
import org.koin.android.ext.android.inject

class NavHostActivity: AppCompatActivity(R.layout.activity_nav_hos) {

    private val navManager: NavManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNavManager()
    }

    private fun initNavManager() {
        navManager.setOnNavEvent {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
            val currentFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
            currentFragment?.findNavController()?.navigate(it)
        }
    }
}