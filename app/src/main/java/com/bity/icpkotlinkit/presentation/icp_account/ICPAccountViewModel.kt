package com.bity.icpkotlinkit.presentation.icp_account

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPTransaction
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.request.AccountBalanceRequest
import com.bity.icp_kotlin_kit.domain.request.AccountTransactionRequest
import com.bity.icp_kotlin_kit.domain.usecase.ICPLedgerCanisterUseCase
import com.bity.icpkotlinkit.presentation.nav.NavManager
import com.bity.icpkotlinkit.util.ext_function.toICPBalance
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.coroutines.CoroutineContext

class ICPAccountViewModel(
    private val navManager: NavManager,
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

    fun onSendClick() {
        publicKey?.let {
            val navDirections = ICPAccountFragmentDirections.actionAccountFragmentToSendFragment(it)
            navManager.navigate(navDirections)
        }
    }

    private suspend fun fetchBalance() {
        icpAccount?.let { account ->
            _uiAccountInformationFlow.value = UiAccountState.Loading
            val balance = icpLedgerCanisterUseCase.accountBalance(
                request = AccountBalanceRequest(
                    account = account,
                    certification = ICPRequestCertification.Certified
                )
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
            val transactions = icpLedgerCanisterUseCase.accountTransactions(
                request = AccountTransactionRequest(
                    address = it.address,
                    certification = ICPRequestCertification.Certified
                )
            ).getOrElse {
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
            val transactions: List<ICPTransaction>
        ): UiTransactionsAccount()
        data object Loading: UiTransactionsAccount()
        data object Error: UiTransactionsAccount()
    }
}