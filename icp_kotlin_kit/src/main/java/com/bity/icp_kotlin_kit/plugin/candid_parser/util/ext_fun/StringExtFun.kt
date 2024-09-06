package com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun

internal fun String.kotlinVariableName() = replaceFirstChar { it.lowercase() }

internal fun String.classNameFromVariableName() =
    split("_")
        .joinToString("") { s ->
            s.replaceFirstChar {
                c -> c.uppercase()
            }
        }

internal fun String.trimCommentLine() =
    this.removeRange(0..1)
        .replace("\\s+".toRegex(), " ")
        .trimStart()

internal fun String.trimEndOfLineComment() =
    // Remove ;
    removeRange(0..1)
        // Remove extra spaces before //
        .trimStart()
        // Remove //
        .removeRange(0..2)
        .replace("\\s".toRegex(), " ")

internal fun String.toKotlinMultiLineComment(): String =
    """
        /**
         * ${lines().joinToString("\n* ")}
         */
    """.trimIndent()

internal fun String.toKotlinFileString(): String {
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