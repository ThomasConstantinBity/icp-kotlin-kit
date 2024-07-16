package com.bity.icp_kotlin_kit.domain.model.error

import java.math.BigInteger

sealed class OrderIndependentHashError(
    errorMessage: String? = null,
    throwable: Throwable? = null
): Error(errorMessage, throwable) {
    class UnsupportedDataType(val value: Any): OrderIndependentHashError()
    class NonUtf8String(val string: String): OrderIndependentHashError()
    class NonPositiveNumber(val number: BigInteger): OrderIndependentHashError()
    class NonASCIIString(val value: ByteArray): OrderIndependentHashError()
}