package com.bity.icp_kotlin_kit.util.ext_function

import java.io.InputStream

// Little-endian = Least significant byte first
val ULong.bytes: ByteArray
    get() = this.toLong().bytes

fun ULong.Companion.readFrom(stream: InputStream): ULong {
    val byteArray = ByteArray(SIZE_BYTES)
    stream.read(byteArray, 0, SIZE_BYTES)
    byteArray.reverse()
    return byteArray.toLong().toULong()
}