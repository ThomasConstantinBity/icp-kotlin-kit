package com.bity.icp_cryptography.util.ext_function

internal fun String.grouped(separator: String, groupLength: Int): String {
    val stringBuilder = StringBuilder()
    forEachIndexed { index, char ->
        if(index != 0 && index % groupLength == 0) {
            stringBuilder.append(separator)
        }
        stringBuilder.append(char)
    }
    return stringBuilder.toString()
}