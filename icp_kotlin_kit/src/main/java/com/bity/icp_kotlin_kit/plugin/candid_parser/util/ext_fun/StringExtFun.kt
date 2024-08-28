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

fun String.toKotlinFileString(): String {
    val kotlinFile = StringBuilder()
    var indent = 0
    this.lines().forEach {

        if(indent > 0 && (it.trimStart().startsWith(")") || it.trimStart().startsWith("}")))
            indent--

        val text = it.trim()
        when {
            text.startsWith("*") ->  kotlinFile.append("${"\t".repeat(indent)} ${text}\n")
            else -> kotlinFile.append("${"\t".repeat(indent)}${text}\n")
        }


        when {
            it.trim().startsWith("*") || it.startsWith("/*") -> { }
            it.endsWith("{") || it.endsWith("(") -> indent++
            it.endsWith("()") -> { }
            indent > 0 && (it.endsWith("}") || it.endsWith(")")) -> indent--
        }
    }
    return kotlinFile.toString()
}