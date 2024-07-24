package com.bity.icpkotlinkit.presentation.send

import androidx.lifecycle.ViewModel
import com.bity.icp_kotlin_kit.domain.usecase.ICPLedgerCanisterUseCase
import com.bity.icpkotlinkit.presentation.nav.NavManager

class SendViewModel(
    private val navManager: NavManager,
    private val icpLedgerCanisterUseCase: ICPLedgerCanisterUseCase
): ViewModel() {

}