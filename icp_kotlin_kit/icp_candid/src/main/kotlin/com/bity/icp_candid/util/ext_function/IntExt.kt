package com.bity.icp_candid.util.ext_function

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Little-endian = Least significant byte first
val Int.bytes: ByteArray
    get() = ByteBuffer
        .allocate(Int.SIZE_BYTES)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putInt(this)
        .array()

fun Int.Companion.readFrom(stream: InputStream): Int {
    val byteArray = ByteArray(SIZE_BYTES)
    stream.read(byteArray, 0, SIZE_BYTES)
    byteArray.reverse()
    return byteArray.toInt()
}