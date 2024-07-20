package com.bity.icpkotlinkit.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.bity.icpkotlinkit.presentation.compose.composable.ProgressIndicator

class MainFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen() {
    ProgressIndicator()
}