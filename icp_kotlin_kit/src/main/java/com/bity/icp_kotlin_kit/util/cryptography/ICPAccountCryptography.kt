package com.bity.icp_kotlin_kit.util.cryptography

import com.bity.icp_kotlin_kit.domain.model.ICPDomainSeparator
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.error.ICPAccountError
import com.bity.icp_cryptography.util.CRC32
import com.bity.icp_cryptography.util.SHA224

object ICPAccountCryptography {

    private val domain = ICPDomainSeparator("account-id")

    internal fun generateAccountId(
        principal: ICPPrincipal,
        subAccountId: ByteArray
    ): ByteArray {
        require(subAccountId.size == 32) {
            throw ICPAccountError.InvalidSubAccountId("subAccountId length must be 32")
        }
        val data = domain.data + principal.bytes + subAccountId
        val hashed = SHA224(data)
        val checksum = CRC32(hashed)
        return checksum + hashed
    }
}