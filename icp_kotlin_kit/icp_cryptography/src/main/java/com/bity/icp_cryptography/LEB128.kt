package com.bity.icp_cryptography

import java.io.IOException
import java.io.InputStream
import java.math.BigInteger

object LEB128 {
    fun encodeUnsigned(value: Number): ByteArray =
        when(value::class) {
            Byte::class -> encodeUnsignedBigInt((value as Byte).toInt().toBigInteger())
            Short::class -> encodeUnsignedBigInt(value.toInt().toBigInteger())
            Int::class -> encodeUnsignedBigInt((value as Int).toBigInteger())
            Long::class -> encodeUnsignedBigInt((value as Long).toBigInteger())
            BigInteger::class -> encodeUnsignedBigInt((value as BigInteger))
            else -> throw Exception("Class ${value::class} not supported")
        }

    fun encodeUnsigned(byte: UByte): ByteArray =
        encodeUnsigned(byte.toLong())

    fun encodeUnsigned(uShort: UShort): ByteArray =
        encodeUnsigned(uShort.toLong())

    fun encodeUnsigned(uInt: UInt): ByteArray =
        encodeUnsigned(uInt.toLong())

    fun encodeUnsigned(uLong: ULong): ByteArray =
        encodeUnsigned(uLong.toLong())

    private fun encodeUnsignedBigInt(bigInt: BigInteger): ByteArray {
        var value = bigInt
        val bytes = mutableListOf<Byte>()
        do {
            var byte = (value and 0x7F.toBigInteger()).toByte()
            value = value shr 7
            if (value != BigInteger.ZERO) {
                byte = (byte.toInt() or 0x80).toByte()
            }
            bytes.add(byte)
        } while (value != BigInteger.ZERO)
        return bytes.toByteArray()
    }

    fun encodeSigned(value: Number): ByteArray =
        when(value::class) {
            Byte::class -> encodeSignedBigInt(value.toInt().toBigInteger())
            Short::class -> encodeSignedBigInt(value.toInt().toBigInteger())
            Int::class -> encodeSignedBigInt((value as Int).toBigInteger())
            Long::class -> encodeSignedBigInt((value as Long).toBigInteger())
            BigInteger::class -> encodeSignedBigInt((value as BigInteger))
            else -> throw Exception("Class ${value::class} not supported")
        }

    private fun encodeSignedBigInt(bigInt: BigInteger): ByteArray {
        var value = bigInt
        var more = true
        val bytes = mutableListOf<Byte>()

        while (more) {
            var byte: Byte = (value and BigInteger.valueOf(0x7F)).toByte()
            value = value shr 7
            if ((value == BigInteger.ZERO && (byte.toInt() shr 6) == 0) ||
                (value == BigInteger.valueOf(-1) && (byte.toInt() shr 6) == 1)) {
                more = false
            } else {
                byte = (byte.toInt() or 0x80).toByte()
            }

            bytes.add(byte)
        }
        return bytes.toByteArray()
    }

    @Throws(IOException::class)
    // Can't define T: Number because the U* classes are not extending Number
    inline fun <reified T> decodeUnsigned(stream: InputStream): T {
        var result = BigInteger.ZERO
        var shift = 0
        var uint8: Int
        do {
            uint8 = stream.read()
            if (uint8 == -1) {
                throw IOException("End of stream reached.")
            }
            result = result.or(BigInteger.valueOf((0x7F and uint8).toLong()).shiftLeft(shift))
            shift += 7
        } while ((uint8 and 0x80) != 0)

        return when (T::class) {
            UByte::class -> result.toByte().toUByte()
            UShort::class -> result.toShort().toUShort()
            UInt::class -> result.toInt().toUInt()
            ULong::class -> result.toLong().toULong()
            Int::class -> result.toInt()
            Long::class -> result.toLong()
            BigInteger::class -> result
            else -> throw IllegalArgumentException("Unsupported type: ${T::class.simpleName}")
        } as T
    }

    @Throws(IOException::class)
    inline fun <reified T : Number> decodeSigned(stream: InputStream): T {
        var result = BigInteger.ZERO
        var shift = 0
        var byte: UByte
        do {
            byte = stream.read().toUByte()
            result = result.or(BigInteger.valueOf((0x7F and byte.toInt()).toLong()).shiftLeft(shift))
            shift += 7
        } while ((byte and 0x80.toUByte()) != 0.toUByte())

        // Check if the highest bit of the last decoded byte is set
        if (byte and 0x40.toUByte() != 0.toUByte()) {
            // Extend the sign to preserve the unsigned interpretation
            result = result.or(BigInteger.valueOf(-1).shiftLeft(shift))
        }

        return when (T::class) {
            Byte::class -> result.toByte()
            Short::class -> result.toShort()
            Int::class -> result.toInt()
            Long::class -> result.toLong()
            Float::class -> result.toFloat()
            BigInteger::class -> result
            else -> throw IllegalArgumentException("Unsupported type: ${T::class.simpleName}")
        } as T
    }
}