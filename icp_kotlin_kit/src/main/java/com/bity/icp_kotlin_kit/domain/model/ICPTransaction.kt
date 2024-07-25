package com.bity.icp_kotlin_kit.domain.model

import java.math.BigInteger

class ICPTransaction(
    val type: ICPTransactionType,
    val amount: BigInteger,
    val fee: BigInteger?,
    val hash: ByteArray,
    val blockIndex: BigInteger,
    val memo: ULong,
    val createdNanos: Long
)