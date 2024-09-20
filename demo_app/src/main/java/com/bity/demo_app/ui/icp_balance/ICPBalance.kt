package com.bity.demo_app.ui.icp_balance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.bity.demo_app.ui.util.AccountID
import com.bity.demo_app.ui.util.LoadingDialog
import com.bity.demo_app.ui.util.TopBar
import java.math.BigDecimal

@Composable
fun AddressBalance(
    viewModel: ICPBalanceViewModel = koinViewModel()
) {
    var accountId by rememberSaveable { mutableStateOf("") }
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopBar("ICP Balance")
        },
        bottomBar = {
            BottomBar(
                isVisible = accountId != "",
                onClick = { viewModel.getICPBalance(accountId) }
            )
        }
    ) {
        LoadingDialog(isLoading = state.isLoading)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 12.dp)
        ) {
            AccountID(
                accountId = accountId,
                onValueChange = { value ->
                    accountId = value
                }
            )
            AccountBalance(state.balance)
            ErrorMessage(message = state.error)
        }
    }
}

@Composable
private fun BottomBar(
    isVisible: Boolean,
    onClick: () -> Unit
) {
    if(isVisible) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Button(
                onClick = { onClick() }
            ) {
                Text(
                    text = "Get Balance"
                )
            }
        }
    }
}

@Composable
private fun AccountBalance(balance: BigDecimal) {
    Text(
        modifier = Modifier
            .padding(vertical = 8.dp),
        text = "Account balance: $balance ICP"
    )
}

@Composable
private fun ErrorMessage(message: String?) {
    message?.let {
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            text = "Error: $it"
        )
    }
}