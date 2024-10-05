package com.bity.icp_kotlin_kit.util.cryptography

import com.bity.icp_kotlin_kit.cryptography.CRC32
import com.bity.icp_kotlin_kit.domain.model.ICPDomainSeparator
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.error.ICPAccountError
import com.bity.icp_kotlin_kit.cryptography.SHA224
import com.bity.icp_kotlin_kit.util.ext_function.fromHex

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

    fun validateAccountId(accountId: String): Boolean {
        val data = accountId.fromHex() ?: return false
        require(data.size == 32) {
            return false
        }
        val checksum = data.take(CRC32.CRC_32_LENGTH)
        val hashed = data.takeLast(data.size - CRC32.CRC_32_LENGTH)
        val expectedChecksum = CRC32(hashed.toByteArray())
        return expectedChecksum.contentEquals(checksum.toByteArray())
    }
}