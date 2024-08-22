package com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun

fun String.trimCommentLine() =
    this.removeRange(0..1)
        .replace("\\s+".toRegex(), " ")
        .trimStart()