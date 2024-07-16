package com.bity.icp_kotlin_kit.domain.model

import com.bity.icp_cryptography.ICPCryptography
import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPPrincipalApiModel

// Source:  from https://internetcomputer.org/docs/current/references/ic-interface-spec/#principal
class ICPPrincipal private constructor(
    val bytes: ByteArray,
    val string: String
) {

    companion object {
        fun init(bytes: ByteArray): ICPPrincipal {
            val string = ICPCryptography.encodeCanonicalText(bytes)
            return ICPPrincipal(
                string = string,
                bytes = bytes
            )
        }

        fun init(string: String): ICPPrincipal {
            val bytes = ICPCryptography.decodeCanonicalText(string)
            return ICPPrincipal(
                string = string,
                bytes = bytes
            )
        }
    }
}

fun ICPPrincipal.toDataModel(): ICPPrincipalApiModel =
    ICPPrincipalApiModel(
        bytes = bytes,
        string = string
    )