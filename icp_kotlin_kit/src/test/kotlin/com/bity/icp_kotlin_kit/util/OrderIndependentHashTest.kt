package com.bity.icp_kotlin_kit.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class OrderIndependentHashTest {

    @ParameterizedTest(name = "Order Independent Hash of {0}")
    @OptIn(ExperimentalStdlibApi::class)
    @MethodSource("orderIndependentHashTestValues")
    fun orderIndependentHash(
        value: Any,
        expectedResult: String
    ) {
        assertEquals(
            expectedResult,
            OrderIndependentHash(value).toHexString()
        )
    }

    companion object {
        @JvmStatic
        private fun orderIndependentHashTestValues() = listOf(
            Arguments.of(0, "6e340b9cffb37a989ca544e6bb780a2c78901d3fb33738768511a30617afa01d"),
            Arguments.of(624485, "7de22b086fa8329c7213ff319a44dc2ca81e23eea99f5fd8bd72222d4ffcb6c2"),
            Arguments.of("abcd", "88d4266fd4e6338d13b845fcf289579d209c897823b9217da3e161936f031589"),
            Arguments.of(
                byteArrayOf(0x47, 0x98.toByte(), 0xfd.toByte()),
                "e9a1e52e12221ae7a1bbad36525258d322c96b83b69aa1d44b3e490ae0e0e17d"
            ),
            Arguments.of(
                listOf(
                    byteArrayOf(0x47, 0x98.toByte(), 0xfd.toByte()),
                    byteArrayOf(0x47, 0x98.toByte(), 0xfd.toByte())
                ),
                "3e98e85312b36683a04d27b3359bc47e7ab4c8296abea5dd2707296018312db8"
            ),
            Arguments.of(
                hashMapOf(
                    "abcd" to byteArrayOf(0x47, 0x98.toByte(), 0xfd.toByte()),
                    "fngt" to byteArrayOf(0x47, 0x98.toByte(), 0xfd.toByte())
                ),
                "0b1085f8ef30c902e25b60f2d7c4a47a71ab7be3da12db2b329b4d8b57ea97fa"
            )
        )
    }
}