package com.bity.icpkotlinkit.presentation.icp_account

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.usecase.ICPLedgerCanisterUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ICPAccountViewModel(
    private val icpLedgerCanisterUseCase: ICPLedgerCanisterUseCase
): ViewModel() {

    private val _uiStateFlow = MutableStateFlow<UiState>(UiState.Content())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    fun onEnter(accountPrincipal: String) {
        viewModelScope.launch {
            _uiStateFlow.value = UiState.Loading(accountPrincipal)
            val result = icpLedgerCanisterUseCase.accountBalance(
                account = ICPAccount.mainAccount(
                    principal = ICPPrincipal.init(accountPrincipal)
                ),
                certification = ICPRequestCertification.Uncertified
            )
            delay(2_000)
            _uiStateFlow.value = UiState.Content(
                accountPrincipal,
                BigDecimal.valueOf(result.toDouble() / 100000000)
            )
        }
    }

    @Immutable
    sealed class UiState {
        data class Content(
            val icpPrincipal: String? = null,
            val balance: BigDecimal? = null
        ) : UiState()
        data class Loading(
            val icpPrincipal: String
        ) : UiState()
        data class Error(
            val icpPrincipal: String
        ) : UiState()
    }

    companion object {
        private const val TAG = "ICPAccountViewModel"
    }
}