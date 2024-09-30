package com.bity.icp_kotlin_kit.domain.model

import java.math.BigDecimal
import java.math.BigInteger

data class ICPTokenBalance(
    val token: ICPToken,
    val balance: BigInteger
) {
    val decimalBalance: BigDecimal = token.decimal(balance)
}