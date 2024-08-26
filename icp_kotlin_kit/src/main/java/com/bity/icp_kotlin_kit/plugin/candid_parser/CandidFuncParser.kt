package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.FunType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.IDLFunArg
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.*
import guru.zoroark.tegral.niwen.lexer.matchers.matches
import guru.zoroark.tegral.niwen.lexer.niwenLexer
import guru.zoroark.tegral.niwen.parser.dsl.either
import guru.zoroark.tegral.niwen.parser.dsl.emit
import guru.zoroark.tegral.niwen.parser.dsl.expect
import guru.zoroark.tegral.niwen.parser.dsl.item
import guru.zoroark.tegral.niwen.parser.dsl.niwenParser
import guru.zoroark.tegral.niwen.parser.dsl.optional
import guru.zoroark.tegral.niwen.parser.dsl.or
import guru.zoroark.tegral.niwen.parser.dsl.repeated
import guru.zoroark.tegral.niwen.parser.dsl.self

internal object CandidFuncParser {

    private val funcLexer = niwenLexer {
        state {
            "(" isToken Token.LParen
            ")" isToken Token.RParen
            "->" isToken Token.Arrow
            ":" isToken Token.Colon
            "," isToken Token.Comma
            ";" isToken Token.Semi

            "query" isToken Token.Query

            "func" isToken Token.Func
            "opt" isToken Token.Opt

            "text" isToken Token.Text
            "bool" isToken Token.Boolean
            "blob" isToken Token.Blob

            "int" isToken Token.Int

            "nat64" isToken Token.Nat64
            "nat" isToken Token.Nat

            matches("\"([a-zA-Z_][a-zA-Z0-9_]*)\"|([a-zA-Z_][a-zA-Z0-9_]*)") isToken Token.Id
            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val funcParser = niwenParser {

        // TODO, could input param could be optional?
        IDLFun root{
            expect(Token.Func)

            expect(Token.LParen)
            repeated<IDLFun, IDLFunArg> {
                expect(IDLFunArg) storeIn item
                optional { expect(Token.Comma) }
            } storeIn IDLFun::inputParams
            expect(Token.RParen)

            expect(Token.Arrow)

            expect(Token.LParen)
            repeated<IDLFun, IDLFunArg> {
                expect(IDLFunArg) storeIn item
                optional { expect(Token.Comma) }
            } storeIn IDLFun::outputParams
            expect(Token.RParen)

            optional {
                expect(Token.Query)
                emit(FunType.Query) storeIn IDLFun::funType
            }

            optional {
                expect(Token.Semi)
            }
        }

        IDLFunArg {
            either {
                expect(Token.Id) storeIn IDLFunArg::argId
                expect(Token.Colon)
                expect(IDLType) storeIn IDLFunArg::idlType
            } or {
                expect(IDLType) storeIn IDLFunArg::idlType
            }
        }

        IDLType {
            either {
                expect(IDLTypeCustom) storeIn self()
            } or {
                expect(IDLTypeText) storeIn self()
            } or {
                expect(IDLTypeBlob) storeIn self()
            } or {
                expect(IDLTypeInt) storeIn self()
            } or {
                expect(IDLTypeNat) storeIn self()
            } or {
                expect(IDLTypeNat64) storeIn self()
            } or {
                expect(IDLTypePrincipal) storeIn self()
            } or {
                expect(IDLTypeBoolean) storeIn self()
            } or {
                expect(IDLTypeVec) storeIn self()
            }
            optional {
                expect(Token.Semi)
            }
        }

        IDLTypeBoolean { expect(Token.Boolean) }
        IDLTypeBlob { expect(Token.Blob) }
        IDLTypeText { expect(Token.Text) }
        IDLTypePrincipal { expect(Token.Principal) }
        IDLTypeCustom { expect(Token.Id) storeIn IDLTypeCustom::typeDef }
        IDLTypeVec { expect(Token.Vec) storeIn IDLTypeVec::vecDeclaration }

        /**
         * Type Int
         */
        IDLTypeInt { expect(Token.Int) }

        /**
         * Type Nat
         */
        IDLTypeNat { expect(Token.Nat) }
        IDLTypeNat64 { expect(Token.Nat64) }
    }

    fun parseFunc(input: String): IDLFun {
        CandidFileParser.debug(funcLexer, input)
        return funcParser.parse(funcLexer.tokenize(input))
    }
}