package com.bity.icp_kotlin_kit.util

import com.bity.icp_kotlin_kit.cryptography.LEB128
import com.bity.icp_kotlin_kit.cryptography.SHA256
import com.bity.icp_kotlin_kit.data.datasource.api.enum.ContentRequestType
import com.bity.icp_kotlin_kit.data.datasource.api.model.ContentApiModel
import com.bity.icp_kotlin_kit.domain.model.error.OrderIndependentHashError
import com.bity.icp_kotlin_kit.util.ext_function.toHexString
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal object OrderIndependentHash {

    operator fun invoke(
        value: Any,
        hashFunction: (ByteArray) -> ByteArray = SHA256::sha256
    ): ByteArray {
        return if (value is ContentApiModel) {
            val tree = encodeTree(value)
            hash(tree, hashFunction)
        } else {
            hash(value, hashFunction)
        }
    }

    // Work around to encode correctly ICPRequestContent class
    // Could we remove reflect library and create a custom function?
    private fun encodeTree(contentApiModel: ContentApiModel): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        (contentApiModel::class as KClass<ContentApiModel>).memberProperties.forEach { prop ->
            // When obfuscation is enabled prop.get() throws an exception because the field is provate
            prop.isAccessible = true
            prop.get(contentApiModel)?.let { value ->
                if (value is ContentRequestType) {
                    result[prop.name] = value.type
                } else {
                    result[prop.name] = value
                }
            }
        }
        return result
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun hash(
        value: Any,
        hashFunction: (ByteArray) -> ByteArray
    ): ByteArray {
        val dataToHash: ByteArray = when(value) {
            is String -> encode(value)
            is UByte -> encodeUByte(value)
            is UShort -> encodeUShort(value)
            is UInt -> encodeUInt(value)
            is ULong -> encodeULong(value)
            is Byte -> encodeNumber(value)
            is Short -> encodeNumber(value)
            is Int -> encodeNumber(value)
            is Long -> encodeNumber(value)
            is BigInteger -> encodeBigInt(value)
            is ByteArray -> value

            is List<*> -> {
                if(value.isEmpty()) {
                    byteArrayOf()
                } else {
                    value
                        .filterNotNull()
                        .map { hash(it, hashFunction) }
                        .reduce { acc, bytes -> acc + bytes }
                }
            }

            is Map<*, *> -> {
                value
                    .map { (key, value) ->
                        if (key != null && value != null) {
                            val keyAscii = encodeAscii(key)
                            val keyHash = hashFunction(keyAscii)
                            val valueHash = hash(value, hashFunction)
                            keyHash + valueHash
                        } else byteArrayOf()

                    }
                    .sortedBy { it.toHexString() }
                    .fold(byteArrayOf()) { acc: ByteArray, next: ByteArray ->
                        acc + next
                    }
            }

            else -> {
                throw OrderIndependentHashError.UnsupportedDataType(value)
            }
        }
        return hashFunction(dataToHash)
    }

    private fun encode(string: String): ByteArray {
        return try {
            string.toByteArray(Charsets.UTF_8)
        } catch (_: NullPointerException) {
            throw OrderIndependentHashError.NonUtf8String(string)
        }
    }

    private fun encodeNumber(number: Number): ByteArray =
        encodeBigInt(BigInteger.valueOf(number.toLong()))

    private fun encodeBigInt(bigInt: BigInteger): ByteArray {
        require(bigInt.signum() != -1) {
            throw OrderIndependentHashError.NonPositiveNumber(bigInt)
        }
        return LEB128.encodeUnsigned(bigInt)
    }

    private fun encodeUByte(byte: UByte): ByteArray =
        LEB128.encodeUnsigned(byte)

    private fun encodeUShort(uShort: UShort): ByteArray =
        LEB128.encodeUnsigned(uShort)

    private fun encodeUInt(uInt: UInt): ByteArray =
        LEB128.encodeUnsigned(uInt)

    private fun encodeULong(uLong: ULong): ByteArray =
        LEB128.encodeUnsigned(uLong)

    private fun encodeAscii(string: Any): ByteArray {
        string.toString().find { it.code > 127 }?.let {
            throw OrderIndependentHashError.NonASCIIString(string.toString().toByteArray())
        }
        return string.toString().toByteArray(Charsets.UTF_8)
    }
}