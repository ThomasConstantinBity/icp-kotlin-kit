package com.bity.icpkotlinkit.presentation.send

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_cryptography.util.EllipticSign
import com.bity.icp_cryptography.util.SHA256
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.request.QueryBlockRequest
import com.bity.icp_kotlin_kit.domain.request.TransferRequest
import com.bity.icp_kotlin_kit.domain.usecase.ICPLedgerCanisterUseCase
import com.bity.icpkotlinkit.presentation.nav.NavManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigInteger

class SendViewModel(
    private val navManager: NavManager,
    private val icpLedgerCanisterUseCase: ICPLedgerCanisterUseCase
): ViewModel() {

    val receivingAccount = "9c449a153a1d612412dd07c220815aed1e477f8c2468281e1052563c54146afe"

    private var publicKey: ByteArray? = null
    private var principal: ICPPrincipal? = null
    private var account: ICPAccount? = null

    private val _transferStateFlow = MutableStateFlow<UiTransferState>(UiTransferState.Idle)
    val transferStateFlow = _transferStateFlow.asStateFlow()

    @OptIn(ExperimentalStdlibApi::class)
    fun onEnter(uncompressedPublicKey: String) {
        publicKey = uncompressedPublicKey.hexToByteArray()
        principal = ICPPrincipal.selfAuthenticatingPrincipal(uncompressedPublicKey.hexToByteArray())
        account = ICPAccount.mainAccount(
            principal = principal!!
        )
    }

    fun transfer(privateKey: String) {
        account?.let {
            _transferStateFlow.value = UiTransferState.Sending
            val signingPrincipal = object : ICPSigningPrincipal {
                override val principal: ICPPrincipal = this@SendViewModel.principal!!
                override val rawPublicKey: ByteArray = publicKey!!

                override suspend fun sign(message: ByteArray): ByteArray {
                    val hashedMessage = SHA256.sha256(message)
                    val signature = EllipticSign(
                        messageToSign = hashedMessage,
                        privateKey = BigInteger(privateKey)
                    )
                    signature[64] = (signature[64] + 0x1b).toByte()
                    return signature.dropLast(1).toByteArray()
                }
            }

            viewModelScope.launch {
                icpLedgerCanisterUseCase.queryBlock(
                    request = QueryBlockRequest(index = 13068030UL)
                )
            }

            /*val request = TransferRequest(
                sendingAccount = it,
                receivingAddress = receivingAccount,
                amount = 123000U,
                signingPrincipal = signingPrincipal,
                memo = 300U
            )

            // 13068030
            // 13068061
            viewModelScope.launch {
                val response = icpLedgerCanisterUseCase.transfer(request)
                    .getOrElse { t ->
                        _transferStateFlow.value = UiTransferState.Error(t.message)
                        return@launch
                    }
                _transferStateFlow.value = UiTransferState.Completed(response)
            }*/
        }
    }

    @Immutable
    sealed class UiTransferState {
        data object Idle: UiTransferState()
        data class Completed(val blockIndex: ULong): UiTransferState()
        data object Sending: UiTransferState()
        data class Error(val errorMessage: String?): UiTransferState()
    }
}