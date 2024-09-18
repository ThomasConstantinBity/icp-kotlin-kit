package com.bity.demo_app.ui.address_balance

import java.math.BigDecimal

data class AddressBalanceState(
    val isLoading: Boolean = false,
    val balance: BigDecimal = BigDecimal.ZERO,
    val error: String? = null
)