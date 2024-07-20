package com.bity.icpkotlinkit.presentation.icp_account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
import com.bity.icp_kotlin_kit.domain.usecase.ICPLedgerCanisterUseCase
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ICPAccountViewModel(
    private val icpLedgerCanisterUseCase: ICPLedgerCanisterUseCase
): ViewModel() {

    init {
        viewModelScope.launch {
            val result = icpLedgerCanisterUseCase.accountBalance(
                account = ICPAccount.mainAccount(
                    principal = ICPPrincipal.init("mi5lp-tjcms-b77vo-qbfgp-cjzyc-imkew-uowpv-ca7f4-l5fzx-yy6ba-qqe")
                ),
                certification = ICPRequestCertification.Uncertified
            )
            Log.d(TAG, "Account balance: ${BigDecimal.valueOf(result.toLong()).divide(BigDecimal("100000000"))}")
        }
    }

    companion object {
        private const val TAG = "ICPAccountViewModel"
    }
}