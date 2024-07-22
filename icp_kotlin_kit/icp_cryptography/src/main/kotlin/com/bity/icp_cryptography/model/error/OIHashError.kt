package com.bity.icp_cryptography.model.error

import java.math.BigInteger

sealed class OIHashError(errorMessage: String? = null): Error(errorMessage)

class UnsupportedDataType(val value: Any): OIHashError()
class NonUTF8String(val string: String): OIHashError()
class NonPositiveNumber(val number: BigInteger): OIHashError()
class NonASCIIString(val value: ByteArray): OIHashError()