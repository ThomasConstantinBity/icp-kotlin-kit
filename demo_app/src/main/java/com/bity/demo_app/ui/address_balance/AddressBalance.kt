package com.bity.demo_app.ui.address_balance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import com.bity.demo_app.ui.util.LoadingDialog
import java.math.BigDecimal

@Composable
fun AddressBalance(
    viewModel: AddressBalanceViewModel = koinViewModel()
) {
    var accountId by rememberSaveable { mutableStateOf("") }
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopBar()
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
fun TopBar() {
    Text(
        text = "ICP Balance",
        style = MaterialTheme.typography.bodyLarge,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun BottomBar(
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
fun AccountID(
    accountId: String,
    onValueChange: (String) -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            label = {
                Text("Account ID")
            },
            value = accountId,
            onValueChange = { onValueChange(it) },
            modifier = Modifier.weight(3F)
        )
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedButton(
            modifier = Modifier
                .weight(1F),
            onClick = {
                clipboardManager.getText()?.text?.let {
                    onValueChange(it)
                }
            })
        {
            Text(text = "Paste")
        }
    }
}

@Composable
fun AccountBalance(balance: BigDecimal) {
    Text(
        modifier = Modifier
            .padding(vertical = 8.dp),
        text = "Account balance: $balance ICP"
    )
}

@Composable
fun ErrorMessage(message: String?) {
    message?.let {
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            text = "Error: $it"
        )
    }
}