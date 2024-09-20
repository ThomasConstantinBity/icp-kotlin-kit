package com.bity.demo_app.ui.icp_tokens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.bity.demo_app.ui.util.LoadingDialog
import com.bity.demo_app.ui.util.TopBar
import com.bity.icp_kotlin_kit.domain.usecase.Tokens
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ICPTokens(
    viewModel: ICPTokensViewModel = koinViewModel()
) {
    val state = viewModel.state
    Scaffold(
        topBar = {
            TopBar("ICP Tokens")
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            when(state) {
                is ICPTokensState.Error -> Text(text = "Error: ${state.errorMessage ?: ""}")
                is ICPTokensState.ICPTokens -> Tokens(state.tokens)
                ICPTokensState.Loading -> LoadingDialog()
            }
        }
    }
}

@Composable
fun Tokens(tokens: Array<Tokens.token>) {
    LazyColumn {
        items(tokens) { token ->
            TokenRow(token)
        }
    }
}

@Composable
fun TokenRow(token: Tokens.token) {
    BadgedBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        badge = {
            Badge(
                modifier = Modifier
                    .padding(horizontal = 16.dp,)
                    .align(Alignment.BottomEnd)
            ) {
                Text(text = getICRCStandard(token.details))
            }
        }
    ) {
        Card {
            Row(
                modifier = Modifier
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(token.thumbnail)
                        .crossfade(true)
                        .build(),
                    placeholder = null,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(50.dp),
                )
                Column(
                    modifier = Modifier
                        .weight(3.0F),
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = token.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = token.description
                    )
                }
            }
        }
    }
}

private fun getICRCStandard(details: Array<Tokens.token._Class1>): String =
    (details.firstOrNull {
        it.string == "standard"
    }?.detail_value as? Tokens.detail_value.Text)?.string ?: "-"