package com.bity.demo_app.ui.icp_balance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.domain.usecase.LedgerCanister
import kotlinx.coroutines.launch

class ICPBalanceViewModel: ViewModel() {

    var state by mutableStateOf(ICPBalanceState())
        private set

    private val ledgerCanisterService = LedgerCanister.LedgerCanisterService(
        canister = ICPSystemCanisters.Ledger.icpPrincipal
    )

    @OptIn(ExperimentalStdlibApi::class)
    fun getICPBalance(account: String) {
        viewModelScope.launch {
            state = ICPBalanceState(
                isLoading = true
            )
            state = try {
                val request = LedgerCanister.AccountBalanceArgs(
                    account = account.hexToByteArray()
                )
                val response = ledgerCanisterService.account_balance(
                    accountBalanceArgs = request
                )
                val icpBalance = (response.e8s.toLong() / 100000000.toFloat()).toBigDecimal()
                ICPBalanceState(
                    balance = icpBalance
                )
            } catch (t: Throwable) {
                ICPBalanceState(
                    error = t.message
                )
            }
        }
    }
}