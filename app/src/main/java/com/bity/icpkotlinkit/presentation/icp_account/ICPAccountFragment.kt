package com.bity.icpkotlinkit.presentation.icp_account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.bity.icpkotlinkit.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bity.icpkotlinkit.common.res.Dimen
import org.koin.androidx.navigation.koinNavGraphViewModel

class ICPAccountFragment: Fragment() {

    private val model: ICPAccountViewModel by koinNavGraphViewModel(R.id.appNavGraph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView: $model")
        return ComposeView(requireContext()).apply {
            setContent {
                MainScreen(model)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        model.onEnter("mi5lp-tjcms-b77vo-qbfgp-cjzyc-imkew-uowpv-ca7f4-l5fzx-yy6ba-qqe")
    }

        companion object {
        private const val TAG = "ICPAccountFragment"
    }
}

@Composable
private fun MainScreen(viewModel: ICPAccountViewModel) {
    val uiState: ICPAccountViewModel.UiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.padding(Dimen.screenContentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Account information",
        )
        Text("Account principal: mi5lp-tjcms-b77vo-qbfgp-cjzyc-imkew-uowpv-ca7f4-l5fzx-yy6ba-qqe")
        AccountBalance(uiState = uiState)
    }
}

@Composable
private fun AccountBalance(uiState: ICPAccountViewModel.UiState) {
    when(uiState) {
        is ICPAccountViewModel.UiState.Content -> Text("Account balance: ${uiState.balance}")
        is ICPAccountViewModel.UiState.Error -> TODO()
        is ICPAccountViewModel.UiState.Loading -> BalanceLoading()
    }
}

@Composable
fun BalanceLoading() {
    Row {
        Text(text = "Account balance: ")
        Box {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(Dimen.spaceL)
                    .align(Alignment.Center)
            )
        }
    }
}