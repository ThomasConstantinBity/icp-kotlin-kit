package com.bity.demo_app.ui.icp_balance

import java.math.BigDecimal

data class ICPBalanceState(
    val isLoading: Boolean = false,
    val balance: BigDecimal = BigDecimal.ZERO,
    val error: String? = null
)