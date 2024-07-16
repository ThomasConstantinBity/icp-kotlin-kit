package com.bity.icp_cryptography.model

import com.bity.icp_cryptography.util.LEB128

class ICPDomainSeparator(
    val domain: String
) {
    val data: ByteArray = LEB128.encodeUnsigned(domain.length) +
            domain.toByteArray(Charsets.UTF_8)

    fun domainSeparatedData(data: ByteArray): ByteArray =
        this.data + data
}