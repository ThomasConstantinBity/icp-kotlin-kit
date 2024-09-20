package com.bity.demo_app.ui.tokens_balance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bity.demo_app.ui.util.LoadingDialog
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bity.icp_kotlin_kit.domain.usecase.Tokens
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.math.BigInteger

@Composable
fun TokensBalance(
    viewModel: TokensBalanceViewModel = koinViewModel()
) {
    val state = viewModel.state
    var uncompressedPublicKey by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomBar(
                isVisible = uncompressedPublicKey != "",
                onClick = {
                    viewModel.getTokens(uncompressedPublicKey)
                }
            )
        }
    ) {
        when(state) {
            is TokensBalanceState.Error -> Text(text = "Error: ${state.error}")
            TokensBalanceState.Loading -> LoadingDialog()
            is TokensBalanceState.TokenWithBalance ->
                TokensBalanceView(
                    balances = state.balances,
                    uncompressedPubKey = uncompressedPublicKey,
                    padding = it
                ) { value ->
                    uncompressedPublicKey = value
                }
        }
    }
}

@Composable
fun TokensBalanceView(
    balances: List<TokenWithBalanceModel>,
    uncompressedPubKey: String,
    padding: PaddingValues,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 12.dp)
    ) {
        PubKey(uncompressedPubKey = uncompressedPubKey) { onValueChange(it) }
        LazyColumn {
            items(balances) {
                TokenBalanceRow(it)
            }
        }
    }
}

@Composable
fun TokenBalanceRow(tokenWithBalanceModel: TokenWithBalanceModel) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(tokenWithBalanceModel.token.thumbnail)
                        .crossfade(true)
                        .build(),
                    placeholder = null,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(50.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(
                        weight = 4F,
                        fill = true
                    )
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = tokenWithBalanceModel.token.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "${getTokenBalance(tokenWithBalanceModel.token, tokenWithBalanceModel.balance)} ${tokenTicker(tokenWithBalanceModel.token)}",
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

private fun getTokenBalance(token: Tokens.token, balance: BigInteger): BigDecimal {
    (token.details.find { it.string == "decimals" }?.detail_value as? Tokens.detail_value.U64)?.uLong?.let {
        return balance.toBigDecimal().divide(BigDecimal.TEN.pow(it.toInt()))
    } ?: return balance.toBigDecimal()
}

private fun tokenTicker(token: Tokens.token): String =
    (token.details.find { it.string == "symbol" }?.detail_value as? Tokens.detail_value.Text)?.string ?: ""


@Composable
fun PubKey(
    uncompressedPubKey: String,
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
                Text("Uncompressed Public Key")
            },
            value = uncompressedPubKey,
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
private fun TopBar() {
    Text(
        text = "ICP Tokens Balance",
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