package com.bity.icp_kotlin_kit.data.datasource.api.response

import com.bity.icp_kotlin_kit.RustBindings
import com.bity.icp_kotlin_kit.data.datasource.api.model.HashTreeNode
import com.bity.icp_kotlin_kit.domain.model.ICPDomainSeparator
import com.bity.icp_kotlin_kit.domain.model.error.RustBindingsError

class StateCertificateResponse(
    val signature: ByteArray,
    val tree: HashTreeNode
) {

    fun verifySignature() {
        val message = ICPDomainSeparator("ic-state-root").data + tree.hash()
        val signatureResult = RustBindings.blsVerify(
            autograph = signature,
            message = message,
            key = ICP_ROOT_RAW_PUBLIC_KEY
        )
        require(signatureResult == 1) {
            throw RustBindingsError.WrongSignature()
        }
        // TODO: handle delegation
        // TODO: verify object identifiers for public key in case of delegation
    }

    companion object {
        private val ICP_ROOT_RAW_PUBLIC_KEY = byteArrayOf(
            0x81.toByte(), 0x4c, 0x0e, 0x6e, 0xc7.toByte(), 0x1f, 0xab.toByte(), 0x58, 0x3b, 0x08,
            0xbd.toByte(), 0x81.toByte(), 0x37, 0x3c, 0x25, 0x5c, 0x3c, 0x37, 0x1b, 0x2e, 0x84.toByte(),
            0x86.toByte(), 0x3c, 0x98.toByte(), 0xa4.toByte(), 0xf1.toByte(), 0xe0.toByte(), 0x8b.toByte(),
            0x74, 0x23, 0x5d, 0x14, 0xfb.toByte(), 0x5d, 0x9c.toByte(), 0x0c, 0xd5.toByte(), 0x46, 0xd9.toByte(),
            0x68, 0x5f, 0x91.toByte(), 0x3a, 0x0c, 0x0b, 0x2c, 0xc5.toByte(), 0x34, 0x15, 0x83.toByte(),
            0xbf.toByte(), 0x4b, 0x43, 0x92.toByte(), 0xe4.toByte(), 0x67, 0xdb.toByte(), 0x96.toByte(),
            0xd6.toByte(), 0x5b, 0x9b.toByte(), 0xb4.toByte(), 0xcb.toByte(), 0x71, 0x71, 0x12, 0xf8.toByte(),
            0x47, 0x2e, 0x0d, 0x5a, 0x4d, 0x14, 0x50, 0x5f, 0xfd.toByte(), 0x74, 0x84.toByte(), 0xb0.toByte(),
            0x12, 0x91.toByte(), 0x09, 0x1c, 0x5f, 0x87.toByte(), 0xb9.toByte(), 0x88.toByte(), 0x83.toByte(),
            0x46, 0x3f, 0x98.toByte(), 0x09, 0x1a, 0x0b, 0xaa.toByte(), 0xae.toByte()
        )
    }
}