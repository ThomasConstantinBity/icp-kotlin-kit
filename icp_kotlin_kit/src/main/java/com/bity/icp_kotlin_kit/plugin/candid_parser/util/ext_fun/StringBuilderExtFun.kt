package com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun

fun StringBuilder.toKotlinFile(): String {
    val kotlinFile = StringBuilder()
    var indent = 0
    this.lines().forEach {

        if(indent > 0 && it.trimStart().startsWith(")"))
            indent--

        kotlinFile.append("${"\t".repeat(indent)}${it.trim()}\n")

        when {
            it.trim().startsWith("*") || it.startsWith("/*") -> { }
            it.endsWith("{") || it.endsWith("(") -> indent++
            it.endsWith("()") -> { }
            indent > 0 && (it.endsWith("}") || it.endsWith(")")) -> indent--
        }
    }
    return kotlinFile.toString()
}