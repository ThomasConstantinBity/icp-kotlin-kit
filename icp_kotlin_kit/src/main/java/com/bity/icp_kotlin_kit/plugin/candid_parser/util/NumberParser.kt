package com.bity.icp_kotlin_kit.plugin.candid_parser.util

fun parseNumber(number: String): String {
    val filteredChars = number.filter { it != '_' }
    return if (number.startsWith("0x")) filteredChars.drop(2)
    else filteredChars
}