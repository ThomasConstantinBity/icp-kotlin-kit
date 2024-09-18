package com.bity.demo_app.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp

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