package com.bity.icp_kotlin_kit.util.ext_function

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Little-endian = Least significant byte first
val Short.bytes: ByteArray
    get() = ByteBuffer
        .allocate(Short.SIZE_BYTES)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putShort(this)
        .array()

fun Short.Companion.readFrom(stream: InputStream): Short {
    val byteArray = ByteArray(SIZE_BYTES)
    stream.read(byteArray, 0, SIZE_BYTES)
    byteArray.reverse()
    return byteArray.toShort()
}