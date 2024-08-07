package com.bity.icp_kotlin_kit.domain.model

import com.bity.icp_kotlin_kit.cryptography.DER
import com.bity.icp_kotlin_kit.cryptography.ICPCryptography
import com.bity.icp_kotlin_kit.cryptography.SHA224
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPPrincipalApiModel

// Source:  from https://internetcomputer.org/docs/current/references/ic-interface-spec/#principal
class ICPPrincipal private constructor(
    val bytes: ByteArray,
    val string: String
) {

    companion object {

        internal fun init(bytes: ByteArray): ICPPrincipal {
            val string = ICPCryptography.encodeCanonicalText(bytes)
            return ICPPrincipal(
                string = string,
                bytes = bytes
            )
        }

        internal fun init(string: String): ICPPrincipal {
            val bytes = ICPCryptography.decodeCanonicalText(string)
            return ICPPrincipal(
                string = string,
                bytes = bytes
            )
        }

        /** Principal with Self-Authenticating ID
         * These have the form H(ec_public_key) · 0x02 (29 bytes).
         * ec_public_key in raw uncompressed form (65 bytes) 0x04·X·Y
         **/
        fun selfAuthenticatingPrincipal(uncompressedPublicKey: ByteArray): ICPPrincipal {
            val serialized = DER.serialise(uncompressedPublicKey)
            val hash = SHA224(serialized)
            val bytes = hash + 0x02.toByte()
            return init(bytes)
        }
    }
}

fun ICPPrincipal.toDataModel(): ICPPrincipalApiModel =
    ICPPrincipalApiModel(
        bytes = bytes,
        string = string
    )