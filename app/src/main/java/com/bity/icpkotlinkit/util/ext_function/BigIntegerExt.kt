package com.bity.icpkotlinkit.util.ext_function

import java.math.BigDecimal
import java.math.BigInteger

fun BigInteger.toICPBalance(): BigDecimal =
    BigDecimal(this).divide(BigDecimal("100000000"))