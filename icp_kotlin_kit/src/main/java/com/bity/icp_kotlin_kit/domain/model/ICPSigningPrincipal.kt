package com.bity.icp_kotlin_kit.domain.model

import com.bity.icp_cryptography.model.ICPDomainSeparator

interface ICPSigningPrincipal {
    val principal: ICPPrincipal
    val rawPublicKey: ByteArray
    // All implementations of this method must ultimately call
    // `ICPCryptography.ellipticSign` with the appropriate private key and
    // return the result
    suspend fun sign(message: ByteArray, domain: ICPDomainSeparator): ByteArray
}