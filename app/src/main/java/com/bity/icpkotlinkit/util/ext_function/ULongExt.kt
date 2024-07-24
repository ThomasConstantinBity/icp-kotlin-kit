package com.bity.icpkotlinkit.util.ext_function

import java.math.BigDecimal

fun ULong.toICPBalance(): BigDecimal =
    BigDecimal(this.toLong()).divide(BigDecimal("100000000"))