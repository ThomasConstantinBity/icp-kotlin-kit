package com.bity.demo_app.ui.tokens_balance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bity.demo_app.ui.util.AccountID
import org.koin.androidx.compose.koinViewModel

@Composable
fun TokensBalance(
    viewModel: TokensBalanceViewModel = koinViewModel()
) {

    var accountId by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar(
                isVisible = accountId != "",
                onClick = {  }
            )
        }
    ) {
        // LoadingDialog(isLoading = state.isLoading)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 12.dp)
        ) {
            var accountId by rememberSaveable { mutableStateOf("") }
            AccountID(accountId = accountId) { value ->
                accountId = value
            }
        }
    }
}

@Composable
private fun TopBar() {
    Text(
        text = "Tokens Balance",
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