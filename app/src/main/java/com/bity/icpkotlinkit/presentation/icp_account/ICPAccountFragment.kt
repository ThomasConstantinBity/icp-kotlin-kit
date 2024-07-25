package com.bity.icpkotlinkit.presentation.icp_account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.bity.icpkotlinkit.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bity.icp_kotlin_kit.domain.model.RosettaTransaction
import com.bity.icp_kotlin_kit.domain.model.RosettaTransactionType
import com.bity.icpkotlinkit.BuildConfig
import com.bity.icpkotlinkit.common.res.Dimen
import com.bity.icpkotlinkit.util.ext_function.toICPBalance
import org.koin.androidx.navigation.koinNavGraphViewModel
import java.math.BigDecimal
import java.math.BigInteger

class ICPAccountFragment: Fragment() {

    private val model: ICPAccountViewModel by koinNavGraphViewModel(R.id.appNavGraph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MainScreen(model)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val publicKey = BuildConfig.ICP_PUB_KEY
        model.onEnter(publicKey)
    }
}

@Composable
private fun MainScreen(viewModel: ICPAccountViewModel) {
    val uiState: ICPAccountViewModel.UiAccountState by viewModel.uiAccountInformationFlow.collectAsStateWithLifecycle()
    val txState: ICPAccountViewModel.UiTransactionsAccount by viewModel.uiAccountTransactionsFlow.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimen.screenContentPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = Dimen.paddingBig),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Account information",
                fontWeight = FontWeight.Bold,
                fontSize = Dimen.textSizeBig,
            )
            AccountInfo(uiState = uiState)
            Text(
                text = "Account transactions",
                fontWeight = FontWeight.Bold,
                fontSize = Dimen.textSizeMedium,
                modifier = Modifier.padding(Dimen.paddingMedium)
            )
            TransactionList(txState)
            Spacer(modifier = Modifier.weight(1f))
        }
        Button(
            onClick = {
                viewModel.onSendClick()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                // .padding(Dimen.paddingSmall)
        ) {
            Text(text = "Send")
        }
    }

}

@Preview(
    showSystemUi = true,
    showBackground = true,
)
@Composable
private fun TransactionList(
    @PreviewParameter(UiTxPreviewParameterProvider::class) transactionState: ICPAccountViewModel.UiTransactionsAccount
) {
    Column {
        when(transactionState) {
            is ICPAccountViewModel.UiTransactionsAccount.Content ->
                LazyColumn(Modifier.fillMaxSize()) {
                    itemsIndexed(transactionState.transactions) { index, d ->
                        Transaction(d)
                        if (index != transactionState.transactions.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }

            ICPAccountViewModel.UiTransactionsAccount.Error -> TODO()
            ICPAccountViewModel.UiTransactionsAccount.Loading -> CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun Transaction(transaction: RosettaTransaction, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.padding(vertical = Dimen.paddingSmall)
    ) {
        Row(modifier.fillMaxWidth()) {
            Text(
                text = "Transaction hash: ",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(0.33F)
                    .align(Alignment.CenterVertically)
            )
            Text(text = transaction.hash.toHexString())
        }

        Row(modifier.fillMaxWidth()) {
            Text(
                text = "Amount: ",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(0.33F)
                    .align(Alignment.CenterVertically)
            )
            Text(text = "${transaction.amount.toICPBalance()} ICP")
        }

        Row(modifier.fillMaxWidth()) {
            Text(
                text = "Transaction fee: ",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(0.33F)
                    .align(Alignment.CenterVertically)
            )
            Text(text = "${transaction.fee.toICPBalance()} ICP")
        }

        Row(modifier.fillMaxWidth()) {
            Text(
                text = "From address",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(0.33F)
                    .align(Alignment.CenterVertically)
            )
            Text(text = transaction.type.from ?: "-")
        }

        Row(modifier.fillMaxWidth()) {
            Text(
                text = "To address",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(0.33F)
                    .align(Alignment.CenterVertically)
            )
            Text(text = transaction.type.to ?: "-")
        }
    }
}


@Composable
private fun AccountInfo(
    @PreviewParameter(UiStatePreviewParameterProvider::class) uiState: ICPAccountViewModel.UiAccountState
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Account balance: ",
                fontWeight = FontWeight.Bold
            )
            AccountBalance(state = uiState)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Account address: ",
            fontWeight = FontWeight.Bold
        )
        when(uiState) {
            is ICPAccountViewModel.UiAccountState.Content ->
                Text(text = uiState.accountAddress)
            ICPAccountViewModel.UiAccountState.Error -> TODO()
            is ICPAccountViewModel.UiAccountState.Loading ->
                Text(text = "-")
        }
    }
}

@Composable
fun AccountBalance(state: ICPAccountViewModel.UiAccountState) {
    when (state) {
        is ICPAccountViewModel.UiAccountState.Content ->
            Text(text = state.balance.toString())
        ICPAccountViewModel.UiAccountState.Error -> TODO()
        is ICPAccountViewModel.UiAccountState.Loading ->
            LinearProgressIndicator(
                modifier = Modifier.width(42.dp)
            )
    }
}

class UiStatePreviewParameterProvider: PreviewParameterProvider<ICPAccountViewModel.UiAccountState> {
    override val values = sequenceOf(
        ICPAccountViewModel.UiAccountState.Loading,
        ICPAccountViewModel.UiAccountState.Content(
            accountAddress = "address",
            balance = BigDecimal(3)
        ),
    )
}

class UiTxPreviewParameterProvider: PreviewParameterProvider<ICPAccountViewModel.UiTransactionsAccount> {
    override val values = sequenceOf(
        ICPAccountViewModel.UiTransactionsAccount.Content(
            transactions = listOf(
                RosettaTransaction(
                    type = RosettaTransactionType.Send(
                        from = "From address",
                        to = "To address"
                    ),
                    amount = BigInteger.TEN,
                    fee = BigInteger.ONE,
                    hash = byteArrayOf(32),
                    blockIndex = BigInteger.ZERO,
                    memo = 300UL,
                    createdNanos = 2000
                )
            )
        ),
        ICPAccountViewModel.UiTransactionsAccount.Loading
    )
}