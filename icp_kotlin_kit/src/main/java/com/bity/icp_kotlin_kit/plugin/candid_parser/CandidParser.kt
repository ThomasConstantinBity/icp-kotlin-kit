package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.util.lexer

internal object CandidParser {

    fun parse(input: String) {
        TODO()
    }

    // TODO, remove
    fun debug(string: String) {
        val tokens = lexer.tokenize(string)
        tokens.forEachIndexed { index, token ->
            println("[$index]: ${token.tokenType}('${token.string}')")
        }
    }
}