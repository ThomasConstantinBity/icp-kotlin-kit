package com.bity.icp_kotlin_kit.plugin.candid_parser.util

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidFileParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.Token
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceParam
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
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
            ":" isToken Token.Colon

            "text" isToken Token.Text
            "blob" isToken Token.Blob
            "bool" isToken Token.Boolean
            "opt" isToken Token.Opt
            "principal" isToken Token.Principal

            "int" isToken Token.Int

            "nat64" isToken Token.Nat64
            "nat" isToken Token.Nat

            matches("""vec\s+record\s+\{([^{}]*|\{[^{}]*\})*}""") isToken Token.Vec
            matches("""vec\s+(\w+\s+)*\s*(\{([^{}]*|\{[^{}]*\})*})?\w*""") isToken Token.Vec
            matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id

            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val serviceParamParser = niwenParser {

        IDLServiceParam root {
            expect(Token.LParen)
            repeated<IDLServiceParam, IDLType> {
                expect(IDLType) storeIn item
                optional {
                    expect(Token.Comma)
                }
            } storeIn IDLServiceParam::params
            expect(Token.RParen)
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
            } or {
                expect(IDLTypeBoolean) storeIn self()
            }
        }

        IDLTypeBlob {
            optional {
                expect(Token.Id) storeIn IDLTypeBlob::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeBlob::isOptional
            }
            expect(Token.Blob)
        }

        IDLTypeText {
            optional {
                expect(Token.Id) storeIn IDLTypeText::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeText::isOptional
            }
            expect(Token.Text)
        }

        /**
         * Type Int
         */
        IDLTypeInt {
            optional {
                expect(Token.Id) storeIn IDLTypeInt::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeInt::isOptional
            }
            expect(Token.Int)
        }

        /**
         * Type Nat
         */
        IDLTypeNat {
            optional {
                expect(Token.Id) storeIn IDLTypeNat::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeNat::isOptional
            }
            expect(Token.Nat)
        }

        IDLTypeNat64 {
            optional {
                expect(Token.Id) storeIn IDLTypeNat64::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeNat64::isOptional
            }
            expect(Token.Nat64)
        }

        IDLTypeVec {
            optional {
                expect(Token.Id) storeIn IDLTypeVec::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeVec::isOptional
            }
            expect(Token.Vec) storeIn IDLTypeVec::vecDeclaration
        }

        IDLTypeCustom {
            optional {
                expect(Token.Id) storeIn IDLTypeCustom::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeCustom::isOptional
            }
            expect(Token.Id) storeIn IDLTypeCustom::typeDef
        }

        IDLTypeBoolean {
            optional {
                expect(Token.Id) storeIn IDLTypeBoolean::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeBoolean::isOptional
            }
            expect(Token.Boolean) }
    }

    fun parseServiceParam(input: String): IDLServiceParam {
        return serviceParamParser.parse(serviceParamLexer.tokenize(input))
    }
}