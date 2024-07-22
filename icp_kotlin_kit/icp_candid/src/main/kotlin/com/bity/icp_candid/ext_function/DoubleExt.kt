package com.bity.icp_candid.ext_function

import com.bity.icp_candid.ext_function.bytes
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Little-endian = Least significant byte first
val Double.bytes: ByteArray
    get() = this.toBits().bytes

fun Double.Companion.readFrom(stream: InputStream): Double {
    val byteArray = ByteArray(SIZE_BYTES)
    stream.read(byteArray, 0, SIZE_BYTES)

    val byteBuffer = ByteBuffer.wrap(byteArray)
    // Set the byte order to little-endian
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
    return byteBuffer.double
}