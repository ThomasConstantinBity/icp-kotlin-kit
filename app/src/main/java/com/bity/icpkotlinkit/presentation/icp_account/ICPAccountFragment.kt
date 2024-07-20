package com.bity.icpkotlinkit.presentation.icp_account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.bity.icpkotlinkit.R
import com.bity.icpkotlinkit.presentation.compose.composable.ProgressIndicator
import org.koin.androidx.navigation.koinNavGraphViewModel

class ICPAccountFragment: Fragment() {

    private val model: ICPAccountViewModel by koinNavGraphViewModel(R.id.appNavGraph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView: $model")
        return ComposeView(requireContext()).apply {
            setContent {
                MainScreen()
            }
        }
    }
    
    companion object {
        private const val TAG = "ICPAccountFragment"
    }
}

@Composable
private fun MainScreen() {
    ProgressIndicator()
}