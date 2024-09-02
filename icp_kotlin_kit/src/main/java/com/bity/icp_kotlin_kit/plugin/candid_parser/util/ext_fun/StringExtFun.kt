package com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun

fun String.kotlinVariableName() = replaceFirstChar { it.lowercase() }
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

fun String.toKotlinFileString(): String {
    val kotlinFile = StringBuilder()
    var indent = 0
    var previousEmptyLine = -1

    this.lines().forEach {

        val line = when {
            it.trim().startsWith("*") -> { it.trim() }
            else -> it.trim().replace("\\s+".toRegex(), " ")
        }

        when {
            line.startsWith(")") || line.startsWith("}") -> indent--
            else -> { }
        }

        val lineToAppend = when {
            line.startsWith("*") -> " $line"
            else -> line
        }
        kotlinFile.appendLine("""${"\t".repeat(indent)}$lineToAppend""")

        when {
            line.startsWith("*") -> { }
            line.endsWith("(") || line.endsWith("{") -> indent++
        }
    }

    return kotlinFile.toString()
}

private fun String.removeMultipleEmptyLines(): String {
    return this
}

    /*this.lines().forEach {

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
    }*/