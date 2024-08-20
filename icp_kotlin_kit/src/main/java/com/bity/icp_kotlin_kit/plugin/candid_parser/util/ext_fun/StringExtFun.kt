package com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun

fun String.declarationToSingleLine(): String =
    this.replace("\\s+".toRegex(), " ")