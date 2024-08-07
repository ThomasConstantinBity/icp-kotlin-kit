package com.bity.icp_kotlin_kit.util.ext_function

import java.io.InputStream

// Little-endian = Least significant byte first
val UShort.bytes: ByteArray
    get() = this.toShort().bytes

fun UShort.Companion.readFrom(stream: InputStream): UShort {
    val byteArray = ByteArray(SIZE_BYTES)
    stream.read(byteArray, 0, SIZE_BYTES)
    byteArray.reverse()
    return byteArray.toShort().toUShort()
}