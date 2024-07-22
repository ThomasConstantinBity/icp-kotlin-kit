package com.bity.icp_candid.util.ext_function

import java.io.InputStream

fun UByte.Companion.readFrom(stream: InputStream): UByte =
    stream.read().toUByte()