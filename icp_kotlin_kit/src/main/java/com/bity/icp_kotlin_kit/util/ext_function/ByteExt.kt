package com.bity.icp_kotlin_kit.util.ext_function

import java.io.InputStream

fun Byte.Companion.readFrom(stream: InputStream): Byte =
    stream.read().toByte()