package com.bity.icp_kotlin_kit.cryptography

import java.security.SecureRandom

internal fun secureRandomOfLength(byteLength: Int): ByteArray {
    val random = SecureRandom()
    val secureRandom = ByteArray(byteLength)
    random.nextBytes(secureRandom)
    return secureRandom
}