package com.bity.icp_cryptography

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayInputStream
import java.math.BigInteger

@OptIn(ExperimentalStdlibApi::class)
class LEB128Test {
    @MethodSource("byteValues")
    @ParameterizedTest(name = "Decoding {1}")
    fun `decode unsigned Byte value`(
        buf: ByteArray,
        expectedResult: Byte
    ) {
        val stream = ByteArrayInputStream(buf)
        val result = LEB128.decodeUnsigned<UByte>(stream)
        assertEquals(
            expectedResult.toUByte(),
            result,
            "Failed to decode $expectedResult"
        )
    }

    @MethodSource("shortValues")
    @ParameterizedTest(name = "Decoding {1}")
    fun `decode unsigned Short value`(
        buf: ByteArray,
        expectedResult: Short
    ) {
        val stream = ByteArrayInputStream(buf)
        val result = LEB128.decodeUnsigned<UShort>(stream)
        assertEquals(
            expectedResult.toUShort(),
            result,
            "Failed to decode $expectedResult"
        )
    }

    @MethodSource("intValues")
    @ParameterizedTest(name = "Decoding {1}")
    fun `decode unsigned Int value`(
        buf: ByteArray,
        expectedResult: Int
    ) {
        val stream = ByteArrayInputStream(buf)
        val result = LEB128.decodeUnsigned<UInt>(stream)
        assertEquals(
            expectedResult.toUInt(),
            result,
            "Failed to decode $expectedResult"
        )
    }

    @MethodSource("longValues")
    @ParameterizedTest(name = "Decoding {1}")
    fun `decode ULong value`(
        buf: ByteArray,
        expectedResult: Long
    ) {
        val stream = ByteArrayInputStream(buf)
        val result = LEB128.decodeUnsigned<ULong>(stream)
        println("Decoded: $result")
        assertEquals(
            expectedResult.toULong(),
            result,
            "Failed to decode $expectedResult",
        )
    }

    @MethodSource("unsignedBigIntValues")
    @ParameterizedTest(name = "Decoding {1}")
    fun `decode unsigned BigInteger value`(
        buf: ByteArray,
        expectedResult: BigInteger
    ) {
        val stream = ByteArrayInputStream(buf)
        val result = LEB128.decodeUnsigned<BigInteger>(stream)
        assertEquals(
            expectedResult,
            result,
            "Failed to decode $expectedResult"
        )
    }

    @MethodSource("signedIntValues")
    @ParameterizedTest(name = "Decoding {1}")
    fun `decode signed with Int value`(
        buf: ByteArray,
        expectedValue: Int
    ) {
        val stream = ByteArrayInputStream(buf)
        val result = LEB128.decodeSigned<Int>(stream)
        assertEquals(
            expectedValue,
            result,
            "Failed to decode $expectedValue"
        )
        assertEquals(
            0,
            stream.available(),
            "Stream is not empty"
        )
    }

    @MethodSource("signedBigIntValues")
    @ParameterizedTest(name = "Encoding {0}")
    fun `encode signed BigInt`(
        value: BigInteger,
        expectedResult: ByteArray
    ) {
        val result = LEB128.encodeSigned(value)
        assertTrue(expectedResult.contentEquals(result))
    }

    @MethodSource("intValues")
    @ParameterizedTest(name = "Encoding {0}")
    fun `encode Int value`(
        expectedResult: ByteArray,
        value: Int,
    ) {
        val result = LEB128.encodeUnsigned(value)
        assertTrue(
            expectedResult.contentEquals(result),
            "Got ${result.toHexString()}, expected ${expectedResult.toHexString()}"
        )
    }

    @MethodSource("unsignedBigIntValues")
    @ParameterizedTest(name = "Encoding {0}")
    fun `encode unsigned BigInt`(
        expectedResult: ByteArray,
        value: BigInteger
    ) {
        val result = LEB128.encodeUnsigned(value)
        assertTrue(
            expectedResult.contentEquals(result)
        )
    }

    companion object {
        @JvmStatic
        fun signedBigIntValues() = listOf(
            Arguments.of(BigInteger.ZERO, byteArrayOf(0x00)),
            Arguments.of(BigInteger.valueOf(63), byteArrayOf(0x3F)),
            Arguments.of(BigInteger.valueOf(64), byteArrayOf(0xC0.toByte(), 0x00)),
            Arguments.of(BigInteger.valueOf(-63), byteArrayOf(0x41)),
            Arguments.of(BigInteger.valueOf(-64), byteArrayOf(0x40)),
            Arguments.of(BigInteger.valueOf(-65), byteArrayOf(0xBF.toByte(), 0x7F)),
            Arguments.of(BigInteger.valueOf(-128), byteArrayOf(0x80.toByte(), 0x7F)),
            Arguments.of(BigInteger.valueOf(-129), byteArrayOf(0xFF.toByte(), 0x7E)),
            Arguments.of(BigInteger.valueOf(97), byteArrayOf(0xE1.toByte(), 0x00)),
            Arguments.of(BigInteger.valueOf(127), byteArrayOf(0xFF.toByte(), 0x00)),
            Arguments.of(BigInteger.valueOf(512), byteArrayOf(0x80.toByte(), 0x04)),
            Arguments.of(BigInteger.valueOf(-512), byteArrayOf(0x80.toByte(), 0x7C)),
            Arguments.of(BigInteger.valueOf(1000), byteArrayOf(0xE8.toByte(), 0x07)),
            Arguments.of(BigInteger.valueOf(-1000), byteArrayOf(0x98.toByte(), 0x78.toByte())),
            Arguments.of(BigInteger.valueOf(10000), byteArrayOf(0x90.toByte(), 0xCE.toByte(), 0x00)),
            Arguments.of(
                BigInteger.valueOf(-10000),
                byteArrayOf(0xF0.toByte(), 0xB1.toByte(), 0x7F.toByte())
            ),
            Arguments.of(
                BigInteger.valueOf(-999999999),
                byteArrayOf(
                    0x81.toByte(),
                    0xec.toByte(),
                    0x94.toByte(),
                    0xa3.toByte(),
                    0x7c.toByte()
                )
            )
        )

        @JvmStatic
        fun unsignedBigIntValues() = listOf(
            Arguments.of(byteArrayOf(0x00.toByte()), BigInteger.ZERO),
            Arguments.of(byteArrayOf(0x7f.toByte()), BigInteger("127")),
            Arguments.of(byteArrayOf(0x80.toByte(), 0x01.toByte()), BigInteger("128")),
            Arguments.of(byteArrayOf(0xac.toByte(), 0x02.toByte()), BigInteger("300")),
            Arguments.of(byteArrayOf(0xff.toByte(), 0x01.toByte()), BigInteger("255")),
            Arguments.of(byteArrayOf(0xe5.toByte(), 0x8e.toByte(), 0x26.toByte()), BigInteger("624485")),
            Arguments.of(
                byteArrayOf(
                    0xFF.toByte(),
                    0xFF.toByte(),
                    0xFF.toByte(),
                    0xFF.toByte(),
                    0xFF.toByte(),
                    0xFF.toByte(),
                    0xFF.toByte(),
                    0xFF.toByte(),
                    0xFF.toByte(),
                    0x01.toByte()
                ), BigInteger("18446744073709551615")
            ),
        )

        @JvmStatic
        fun signedIntValues() = listOf(
            Arguments.of(byteArrayOf(0x00.toByte()), 0),
            Arguments.of(byteArrayOf(0x3F.toByte()), 63),
            Arguments.of(byteArrayOf(0xC0.toByte(), 0x00.toByte()), 64),
            Arguments.of(
                byteArrayOf(
                    0x41.toByte()
                ), -63
            ),
            Arguments.of(
                byteArrayOf(
                    0x40.toByte()
                ), -64
            ),
            Arguments.of(
                byteArrayOf(
                    0xbf.toByte(), 0x7f.toByte()
                ), -65
            ),
            Arguments.of(
                byteArrayOf(
                    0x80.toByte(), 0x7f.toByte()
                ), -128
            ),
            Arguments.of(
                byteArrayOf(
                    0xff.toByte(), 0x7e.toByte()
                ), -129
            ),
            Arguments.of(
                byteArrayOf(
                    0xe1.toByte(), 0x00.toByte()
                ), 97
            ),
            Arguments.of(
                byteArrayOf(
                    0xff.toByte(), 0x00.toByte()
                ), 127
            ),
            Arguments.of(
                byteArrayOf(
                    0xe5.toByte(), 0x8e.toByte(), 0x26.toByte()
                ), 624485
            ),
            Arguments.of(
                byteArrayOf(
                    0xc0.toByte(), 0xbb.toByte(), 0x78.toByte()
                ), -123456
            ),
            Arguments.of(
                byteArrayOf(
                    0x81.toByte(), 0xec.toByte(), 0x94.toByte(), 0xa3.toByte(), 0x7c.toByte()
                ), -999999999
            ),
        )

        @JvmStatic
        fun byteValues() = listOf(
            Arguments.of(byteArrayOf(0x00.toByte()), 0.toByte()),
        )

        @JvmStatic
        fun shortValues() = listOf(
            Arguments.of(byteArrayOf(0x00.toByte()), 0.toShort()),
            Arguments.of(byteArrayOf(0x7f.toByte()), 127.toShort()),
        )

        @JvmStatic
        fun intValues() = listOf(
            Arguments.of(byteArrayOf(0x00.toByte()), 0),
            Arguments.of(byteArrayOf(0x7f.toByte()), 127),
            Arguments.of(byteArrayOf(0x80.toByte(), 0x01.toByte()), 128),
            Arguments.of(byteArrayOf(0xac.toByte(), 0x02.toByte()), 300),
            Arguments.of(byteArrayOf(0xff.toByte(), 0x01.toByte()), 255),
            Arguments.of(byteArrayOf(0xe5.toByte(), 0x8e.toByte(), 0x26.toByte()), 624485),
        )

        @JvmStatic
        fun longValues() = listOf(
            Arguments.of(byteArrayOf(0x00.toByte()), 0.toLong()),
            Arguments.of(byteArrayOf(0x7f.toByte()), 127.toLong()),
            Arguments.of(byteArrayOf(0x80.toByte(), 0x01.toByte()), 128.toLong()),
            Arguments.of(byteArrayOf(0xac.toByte(), 0x02.toByte()), 300.toLong()),
            Arguments.of(byteArrayOf(0xff.toByte(), 0x01.toByte()), 255.toLong()),
            Arguments.of(byteArrayOf(0xe5.toByte(), 0x8e.toByte(), 0x26.toByte()), 624485.toLong()),
        )
    }
}