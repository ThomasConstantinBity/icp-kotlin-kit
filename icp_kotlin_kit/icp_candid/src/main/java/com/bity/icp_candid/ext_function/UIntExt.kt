package com.bity.icp_candid.ext_function

import java.io.InputStream

// Little-endian = Least significant byte first
val UInt.bytes: ByteArray
    get() = this.toInt().bytes

fun UInt.Companion.readFrom(stream: InputStream): UInt {
    val byteArray = ByteArray(SIZE_BYTES)
    stream.read(byteArray, 0, SIZE_BYTES)
    byteArray.reverse()
    return byteArray.toInt().toUInt()
}