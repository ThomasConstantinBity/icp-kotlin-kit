package com.bity.icp_kotlin_kit.plugin.candid_parser.util

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidFileParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.Token
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceParam
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
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

internal object CandidServiceParamParser {

    private val serviceParamLexer = niwenLexer {
        state {

            "," isToken Token.Comma
            "{" isToken Token.LBrace
            "}" isToken Token.RBrace
            "->" isToken Token.Arrow
            "(" isToken Token.LParen
            ")" isToken Token.RParen
            ";" isToken Token.Semi

            "text" isToken Token.Text
            "blob" isToken Token.Blob
            "opt" isToken Token.Opt
            "principal" isToken Token.Principal

            "int" isToken Token.Int

            "nat64" isToken Token.Nat64
            "nat" isToken Token.Nat

            matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id

            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val serviceParamParser = niwenParser {

        IDLServiceParam root {
            repeated<IDLServiceParam, IDLType> {
                expect(IDLType) storeIn item
                optional {
                    expect(Token.Comma)
                }
            } storeIn IDLServiceParam::params
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
                expect(IDLTypeVec) storeIn self()
            }
        }

        IDLTypeBlob { expect(Token.Blob) }
        IDLTypeText {
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeText::isOptional
            }
            expect(Token.Text)
        }

        /**
         * Type Int
         */
        IDLTypeInt { expect(Token.Int) }

        /**
         * Type Nat
         */
        IDLTypeNat {
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeNat::isOptional
            }
            expect(Token.Nat)
        }
        IDLTypeNat64 { expect(Token.Nat64) }

        IDLTypeCustom { expect(Token.Id) storeIn IDLTypeCustom::typeDef }
        IDLTypeVec { expect(Token.Vec) storeIn IDLTypeVec::vecDeclaration }
    }

    fun parseServiceParam(input: String): IDLServiceParam {
        return serviceParamParser.parse(serviceParamLexer.tokenize(input))
    }
}