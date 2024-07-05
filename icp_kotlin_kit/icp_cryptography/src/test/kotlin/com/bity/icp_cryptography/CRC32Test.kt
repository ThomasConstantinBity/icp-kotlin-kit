package com.bity.icp_cryptography

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@OptIn(ExperimentalStdlibApi::class)
class CRC32Test {

    @ParameterizedTest
    @MethodSource("crc32TestValues")
    fun crc32(
        data: ByteArray,
        expectedValue: ByteArray
    ) {
        val crc32 = CRC32.crc32(data)
        assertArrayEquals(expectedValue, crc32)
    }

    companion object {
        @JvmStatic
        private fun crc32TestValues() = listOf(
            Arguments.of(
                byteArrayOf(),
                "00000000".hexToByteArray()
            ),
            Arguments.of(
                "123456789".toByteArray(),
                "cbf43926".hexToByteArray()
            ),
            Arguments.of(
                "a".toByteArray(),
                "e8b7be43".hexToByteArray()
            ),
            Arguments.of(
                "abc".toByteArray(),
                "352441c2".hexToByteArray()
            ),
            Arguments.of(
                "message digest".toByteArray(),
                "20159d7f".hexToByteArray()
            ),
            Arguments.of(
                "abcdefghijklmnopqrstuvwxyz".toByteArray(),
                "4c2750bd".hexToByteArray()
            ),
            Arguments.of(
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toByteArray(),
                "1fc2e6d2".hexToByteArray()
            ),
            Arguments.of(
                "12345678901234567890123456789012345678901234567890123456789012345678901234567890".toByteArray(),
                "7ca94a72".hexToByteArray()
            )
        )
    }
}