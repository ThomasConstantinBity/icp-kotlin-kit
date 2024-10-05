package com.bity.icp_kotlin_kit.domain.model

import java.math.BigInteger

sealed class ICPTokenTransfer {
    data class Height(
        val height: BigInteger
    ): ICPTokenTransfer()
    data class Amount(
        val amount: String
    ): ICPTokenTransfer()
    data class TransactionId(
        val transactionId: String
    ): ICPTokenTransfer()
}