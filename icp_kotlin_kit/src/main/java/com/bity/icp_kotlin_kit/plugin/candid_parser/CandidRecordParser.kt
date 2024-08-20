package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.util.lexer
import guru.zoroark.tegral.niwen.lexer.matchers.matches
import guru.zoroark.tegral.niwen.lexer.niwenLexer

internal object CandidRecordParser {

    private val recordLexer = niwenLexer {
        state {
            matches("//.*") isToken Token.SingleLineComment
            "{" isToken Token.LBrace
            "}" isToken Token.RBrace
            ";" isToken Token.Semi
            ":" isToken Token.Colon

            "record" isToken Token.Record

            "text" isToken Token.Text
            "blob" isToken Token.Blob

            "int" isToken Token.Int

            "nat64" isToken Token.Nat64
            "nat" isToken Token.Nat

            matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id
            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    /** private val recordParser = niwenParser {

    } **/

    fun parse(input: String) {
        debug(input)
        // return recordParser.parse(lexer.tokenize(input))
    }

    // TODO, remove
    private fun debug(string: String) {
        val tokens = recordLexer.tokenize(string)
        tokens.forEachIndexed { index, token ->
            println("[$index]: ${token.tokenType}('${token.string}')")
        }
    }
}