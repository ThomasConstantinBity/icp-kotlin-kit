package com.bity.icp_kotlin_kit.cryptography

import java.security.MessageDigest

internal object SHA224 {

    private const val SHA_224_ALGORITHM = "SHA-224"
    private val messageDigest =  MessageDigest.getInstance(SHA_224_ALGORITHM)

    operator fun invoke(data: ByteArray): ByteArray =
        messageDigest.digest(data)
}