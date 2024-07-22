package com.bity.icp_cryptography.util

import com.bity.icp_cryptography.model.error.InvalidEcPublicKey
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.asn1.DERBitString
import org.bouncycastle.asn1.DERSequence

object DER {

    // Asymmetric Encryption Algorithms: ECC (ecPublicKey)
    private const val EC_PUBLIC_KEY ="1.2.840.10045.2.1"
    // Asymmetric Encryption Algorithms: ECC (ecPublicKey)
    private const val SECP256K1 = "1.3.132.0.10"

    private val ecPublicKey = ASN1ObjectIdentifier(EC_PUBLIC_KEY)
    private val secp256k1 = ASN1ObjectIdentifier(SECP256K1)

    fun serialise(uncompressedEcPublicKey: ByteArray): ByteArray {
        if (uncompressedEcPublicKey.size != 65
            && uncompressedEcPublicKey.first() != 0x04.toByte()) {
            throw InvalidEcPublicKey()
        }

        val encoded = DERSequence(
            arrayOf(
                DERSequence(arrayOf(ecPublicKey, secp256k1)),
                DERBitString(uncompressedEcPublicKey)
            )
        )
        return encoded.encoded
    }
}