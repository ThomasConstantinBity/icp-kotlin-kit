package com.bity.icp_kotlin_kit.util.ext_function

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Little-endian = Least significant byte first
val Float.bytes: ByteArray
    get() = this.toRawBits().bytes

fun Float.Companion.readFrom(stream: InputStream): Float {
    val byteArray = ByteArray(SIZE_BYTES)
    stream.read(byteArray, 0, SIZE_BYTES)

    val byteBuffer = ByteBuffer.wrap(byteArray)
    // Set the byte order to little-endian
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
    return byteBuffer.float
}