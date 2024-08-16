package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidParser.debug
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLArgs
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLArgsWrapper
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLDecimal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLHex
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLValue
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.lexer
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.parseNumber
import guru.zoroark.tegral.niwen.parser.dsl.either
import guru.zoroark.tegral.niwen.parser.dsl.emit
import guru.zoroark.tegral.niwen.parser.dsl.expect
import guru.zoroark.tegral.niwen.parser.dsl.item
import guru.zoroark.tegral.niwen.parser.dsl.niwenParser
import guru.zoroark.tegral.niwen.parser.dsl.optional
import guru.zoroark.tegral.niwen.parser.dsl.or
import guru.zoroark.tegral.niwen.parser.dsl.repeated
import guru.zoroark.tegral.niwen.parser.dsl.self

internal object CandidArgsParser {

    private val argParser = niwenParser {

        IDLArgsWrapper root {
            expect(IDLArgs) storeIn IDLArgsWrapper::idlArgs
        }

        IDLArgs {
            repeated {
                expect(IDLValue) storeIn item
            } storeIn IDLArgs::args
        }

        IDLValue {
            optional {
                expect(Token.LParen)
            }

            either {
                expect(IDLBoolean) storeIn self()
            } or {
                expect(IDLNull) storeIn self()
            } or {
                expect(IDLDecimal) storeIn self()
            } or {
                expect(IDLText) storeIn self()
            } or {
                expect(IDLHex) storeIn self()
            }

            optional {
                expect(Token.Comma)
            }
            optional {
                expect(Token.RParen)
            }
        }

        IDLBoolean {
            expect(Token.Boolean) transform { it.lowercase() == "true" } storeIn IDLBoolean::value
        }

        IDLNull {
            expect(Token.Null)
        }

        IDLDecimal {
            either {
                expect(Token.Sign) storeIn IDLDecimal::sign
                expect(Token.Decimal) transform { parseNumber(it) } storeIn IDLDecimal::decimal
            } or {
                emit("+") storeIn IDLDecimal::sign
                expect(Token.Decimal) transform { parseNumber(it) } storeIn IDLDecimal::decimal
            }
        }

        IDLText {
            expect(Token.Text) storeIn IDLText::text
        }

        IDLHex {
            expect(Token.Hex) transform { parseNumber(it) } storeIn IDLHex::hexValue
        }
    }

    fun parseArgs(input: String): IDLArgs {
        val tokens = lexer.tokenize(input)
        debug(input)
        val idlArgsWrapper = argParser.parse(tokens)
        debug(idlArgsWrapper.idlArgs)
        return idlArgsWrapper.idlArgs
    }

}