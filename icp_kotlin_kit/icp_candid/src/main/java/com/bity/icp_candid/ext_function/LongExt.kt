package com.bity.icp_candid.ext_function

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

val Long.bytes: ByteArray
    get() = ByteBuffer
        .allocate(Long.SIZE_BYTES)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putLong(this)
        .array()

fun Long.Companion.readFrom(stream: InputStream): Long {
    val byteArray = ByteArray(SIZE_BYTES)
    stream.read(byteArray, 0, SIZE_BYTES)
    byteArray.reverse()
    return byteArray.toLong()
}