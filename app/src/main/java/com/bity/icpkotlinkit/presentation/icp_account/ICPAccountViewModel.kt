package com.bity.icpkotlinkit.presentation.icp_account

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_cryptography.util.EllipticSign
import com.bity.icp_cryptography.util.SHA256
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
import com.bity.icp_kotlin_kit.domain.model.RosettaTransaction
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.request.TransferRequest
import com.bity.icp_kotlin_kit.domain.usecase.ICPLedgerCanisterUseCase
import com.bity.icpkotlinkit.util.ext_function.toICPBalance
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.BigInteger

class ICPAccountViewModel(
    private val icpLedgerCanisterUseCase: ICPLedgerCanisterUseCase
): ViewModel() {

    private var publicKey: String? = null
    private var icpAccount: ICPAccount? = null

    private val _uiAccountInformationFlow = MutableStateFlow<UiAccountState>(UiAccountState.Loading)
    val uiAccountInformationFlow = _uiAccountInformationFlow.asStateFlow()

    private val _uiAccountTransactionsFlow = MutableStateFlow<UiTransactionsAccount>(UiTransactionsAccount.Loading)
    val uiAccountTransactionsFlow = _uiAccountTransactionsFlow.asStateFlow()

    @OptIn(ExperimentalStdlibApi::class)
    fun onEnter(uncompressedPublicKey: String) {
        publicKey = uncompressedPublicKey
        val icpPrincipal = ICPPrincipal.selfAuthenticatingPrincipal(uncompressedPublicKey.hexToByteArray())
        icpAccount = ICPAccount.mainAccount(icpPrincipal)


        viewModelScope.launch {
            val balanceDeferred = async { fetchBalance() }
            val transactionDeferred = async { fetchTransactions() }

            balanceDeferred.await()
            transactionDeferred.await()
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun send(privateKey: String) {
        icpAccount?.let { account ->

            val signingPrincipal = object : ICPSigningPrincipal {
                override val principal: ICPPrincipal = ICPPrincipal.selfAuthenticatingPrincipal(publicKey!!.hexToByteArray())
                override val rawPublicKey: ByteArray = publicKey!!.hexToByteArray()
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

            val request = TransferRequest(
                sendingAccount = account,
                receivingAddress = account.address,
                amount = 10000U,
                signingPrincipal = signingPrincipal,
                memo = 300U
            )
            viewModelScope.launch {
                icpLedgerCanisterUseCase.transfer(request).getOrThrow()
            }
        }
    }

    private suspend fun fetchBalance() {
        icpAccount?.let { account ->
            _uiAccountInformationFlow.value = UiAccountState.Loading
            val balance = icpLedgerCanisterUseCase.accountBalance(
                account = account,
                certification = ICPRequestCertification.Uncertified
            ).getOrElse {
                _uiAccountInformationFlow.value = UiAccountState.Error
                return
            }
            _uiAccountInformationFlow.value = UiAccountState.Content(
                accountAddress = account.address,
                balance = balance.toICPBalance()
            )
        }
    }

    private suspend fun fetchTransactions() {
        icpAccount?.let {
            _uiAccountTransactionsFlow.value = UiTransactionsAccount.Loading
            val transactions = icpLedgerCanisterUseCase.accountTransactions(it.address).getOrElse {
                _uiAccountTransactionsFlow.value = UiTransactionsAccount.Error
                return
            }
            _uiAccountTransactionsFlow.value = UiTransactionsAccount.Content(
                transactions = transactions
            )
        }
    }

    @Immutable
    sealed class UiAccountState {
        data class Content(
            val accountAddress: String,
            val balance: BigDecimal
        ) : UiAccountState()
        data object Loading : UiAccountState()
        data object Error : UiAccountState()
    }

    @Immutable
    sealed class UiTransactionsAccount {
        data class Content(
            val transactions: List<RosettaTransaction>
        ): UiTransactionsAccount()
        data object Loading: UiTransactionsAccount()
        data object Error: UiTransactionsAccount()
    }

    companion object {
        private const val TAG = "ICPAccountViewModel"
    }
}