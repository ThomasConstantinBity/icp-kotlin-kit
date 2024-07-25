package com.bity.icpkotlinkit.presentation.send

import android.os.Bundle
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
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import com.bity.icpkotlinkit.BuildConfig
import com.bity.icpkotlinkit.R
import com.bity.icpkotlinkit.common.res.Dimen
import org.koin.androidx.navigation.koinNavGraphViewModel

class SendFragment: Fragment() {

    private val args: SendFragmentArgs by navArgs()
    private val model: SendViewModel by koinNavGraphViewModel(R.id.sendFragment)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        model.onEnter(args.publicKey)

        return ComposeView(requireContext()).apply {
            setContent {
                SendScreen(model)
            }
        }
    }
}

@Composable
fun SendScreen(model: SendViewModel) {
    val transferState: SendViewModel.UiTransferState by model.transferStateFlow.collectAsStateWithLifecycle()
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
                text = "Send ICP",
                fontWeight = FontWeight.Bold,
                fontSize = Dimen.textSizeBig,
            )
            Text(
                text = "Send 0.00123 ICP to ${model.receivingAccount}",
            )
            SendingInfo(transferState)
            Spacer(modifier = Modifier.weight(1f))
        }
        if(transferState is SendViewModel.UiTransferState.Idle) {
            Button(
                onClick = { model.transfer(BuildConfig.ICP_PRIV_KEY) },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(text = "Send")
            }
        }
    }
}

@Composable
fun SendingInfo(
    transferState: SendViewModel.UiTransferState
) {
    when(transferState) {
        is SendViewModel.UiTransferState.Completed -> 
            Text(
                text = "Transaction sent - block index: ${transferState.blockIndex}",
                modifier = Modifier
                    .padding(vertical = Dimen.paddingMedium)
                    .fillMaxWidth()
            )
        is SendViewModel.UiTransferState.Error -> 
            Text(text = "Error sending transaction: ${transferState.errorMessage ?: ""}")
        SendViewModel.UiTransferState.Idle -> { }
        SendViewModel.UiTransferState.Sending -> 
            Row(
                modifier = Modifier
                    .padding(vertical = Dimen.paddingMedium)
                    .fillMaxWidth()
            ) {
                Text(text = "Sending")
                LinearProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = Dimen.paddingMedium)
                        .fillMaxWidth()
                )
            }
    }
}