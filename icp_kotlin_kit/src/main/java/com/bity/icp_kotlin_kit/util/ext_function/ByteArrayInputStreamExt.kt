package com.bity.icp_kotlin_kit.util.ext_function

import java.io.ByteArrayInputStream

fun ByteArrayInputStream.readNextBytes(length: Int): ByteArray {
    val buffer = ByteArray(length)
    val bytesRead = read(buffer, 0, length)
    require(bytesRead == length)
    return buffer
}