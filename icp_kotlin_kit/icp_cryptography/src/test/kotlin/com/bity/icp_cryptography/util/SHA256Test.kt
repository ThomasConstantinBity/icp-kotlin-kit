package com.bity.icp_cryptography.util

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class SHA256Test {

    @ParameterizedTest
    @MethodSource("sha256TestValues")
    fun sha256(
        data: ByteArray,
        expectedResult: ByteArray
    ) {
        val sha256 = SHA256.sha256(data)
        assertArrayEquals(expectedResult, sha256)
    }

    @ParameterizedTest
    @MethodSource("doubleSha256TestValues")
    fun doubleSha256(
        data: ByteArray,
        expectedResult: ByteArray
    ) {
        val doubleSha256 = SHA256.doubleSha256(data)
        assertArrayEquals(expectedResult, doubleSha256)
    }

    @OptIn(ExperimentalStdlibApi::class)
    companion object {
        @JvmStatic
        private fun sha256TestValues() = listOf(
            Arguments.of(
                byteArrayOf(),
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855".hexToByteArray()
            ),
            Arguments.of(
                "abc".toByteArray(),
                "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad".hexToByteArray()
            ),
            Arguments.of(
                "de188941a3375d3a8a061e67576e926d".hexToByteArray(),
                "067c531269735ca7f541fdaca8f0dc76305d3cada140f89372a410fe5eff6e4d".hexToByteArray()
            ),
            Arguments.of(
                "de188941a3375d3a8a061e67576e926dc71a7fa3f0cceb97452b4d3227965f9ea8cc75076d9fb9c5417aa5cb30fc22198b34982dbb629e".hexToByteArray(),
                "038051e9c324393bd1ca1978dd0952c2aa3742ca4f1bd5cd4611cea83892d382".hexToByteArray()
            ),
            Arguments.of(
                StringBuilder().apply { for(i in 0 until 1_000_000) { this.append("a") } }.toString().toByteArray(),
                "cdc76e5c9914fb9281a1c7e284d73e67f1809a48a497200e046d39ccc7112cd0".hexToByteArray()
            )
        )

        @JvmStatic
        private fun doubleSha256TestValues() = listOf(
            Arguments.of(
                byteArrayOf(),
                "5df6e0e2761359d30a8275058e299fcc0381534545f55cf43e41983f5d4c9456".hexToByteArray()
            ),
            Arguments.of(
                "abc".toByteArray(),
                "4f8b42c22dd3729b519ba6f68d2da7cc5b2d606d05daed5ad5128cc03e6c6358".hexToByteArray()
            ),
            Arguments.of(
                "de188941a3375d3a8a061e67576e926d".hexToByteArray(),
                "2182d3fe9882fd597d25daf6a85e3a574e5a9861dbc75c13ce3f47fe98572246".hexToByteArray()
            ),
            Arguments.of(
                "de188941a3375d3a8a061e67576e926dc71a7fa3f0cceb97452b4d3227965f9ea8cc75076d9fb9c5417aa5cb30fc22198b34982dbb629e".hexToByteArray(),
                "3b4666a5643de038930566a5930713e65d72888d3f51e20f9545329620485b03".hexToByteArray()
            ),
            Arguments.of(
                StringBuilder().apply { for(i in 0 until 1_000_000) { this.append("a") } }.toString().toByteArray(),
                "80d1189477563e1b5206b2749f1afe4807e5705e8bd77887a60187a712156688".hexToByteArray()
            )
        )
    }
}