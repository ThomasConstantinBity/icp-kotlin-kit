package com.bity.icp_cryptography.util

import java.security.SecureRandom

fun secureRandomOfLength(byteLength: Int): ByteArray {
    val random = SecureRandom()
    val secureRandom = ByteArray(byteLength)
    random.nextBytes(secureRandom)
    return secureRandom
}