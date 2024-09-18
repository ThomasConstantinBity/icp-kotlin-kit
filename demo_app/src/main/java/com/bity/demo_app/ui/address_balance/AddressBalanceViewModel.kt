package com.bity.demo_app.ui.address_balance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.domain.usecase.LedgerCanister
import kotlinx.coroutines.launch

class AddressBalanceViewModel: ViewModel() {

    var state by mutableStateOf(AddressBalanceState())
        private set

    private val ledgerCanisterService = LedgerCanister.LedgerCanisterService(
        canister = ICPSystemCanisters.Ledger.icpPrincipal
    )

    @OptIn(ExperimentalStdlibApi::class)
    fun getICPBalance(account: String) {
        viewModelScope.launch {
            state = AddressBalanceState(
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
                AddressBalanceState(
                    balance = icpBalance
                )
            } catch (t: Throwable) {
                AddressBalanceState(
                    error = t.message
                )
            }
        }
    }
}