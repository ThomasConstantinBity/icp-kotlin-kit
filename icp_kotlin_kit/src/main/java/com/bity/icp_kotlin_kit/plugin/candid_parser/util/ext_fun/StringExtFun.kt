package com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun

fun String.trimCommentLine() =
    this.removeRange(0..1)
        .replace("\\s+".toRegex(), " ")
        .trimStart()

fun String.trimEndOfLineComment() =
    // Remove ;
    removeRange(0..1)
        // Remove extra spaces before //
        .trimStart()
        // Remove //
        .removeRange(0..2)
        .replace("\\s".toRegex(), " ")

fun String.trimVecRecord() =
    removeRange(0..3)
        .trimStart()