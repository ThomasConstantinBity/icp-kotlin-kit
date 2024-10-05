package com.bity.icp_kotlin_kit.file_parser.candid_parser.util.ext_fun

internal fun String.kotlinVariableName() = replaceFirstChar { it.lowercase() }

internal fun String.trimCommentLine() =
    this.removeRange(0..1)
        .replace("\\s+".toRegex(), " ")
        .trimStart()