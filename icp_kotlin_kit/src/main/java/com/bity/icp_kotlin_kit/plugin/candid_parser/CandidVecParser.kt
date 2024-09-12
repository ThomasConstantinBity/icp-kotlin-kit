package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_vec.IDLVec
import guru.zoroark.tegral.niwen.lexer.matchers.matches
import guru.zoroark.tegral.niwen.lexer.matchers.repeated
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

internal object CandidVecParser {

    private val vecLexer = niwenLexer {
        state {
            matches("//.*") isToken Token.SingleLineComment

            ":" isToken Token.Colon

            "text" isToken Token.Text

            matches("""vec(?!.*vec)""") isToken Token.Vec
            matches("""vec.*vec.*""")isToken  Token.VecDeclaration
            matches("""record\s+\{(?:[^{}]|\{(?:[^{}]|\{(?:[^{}]|\{[^{}]*\})*\})*\})*\}""") isToken Token.Record

            "service" isToken Token.Service

            "blob" isToken Token.Blob
            "opt" isToken Token.Opt

            "int" isToken Token.Int

            "nat64" isToken Token.Nat64
            "nat" isToken Token.Nat

            matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id

            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val vecParser = niwenParser {
        IDLVec root {

            optional {
                expect(Token.Id) storeIn IDLVec::id
                expect(Token.Colon)
            }

            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLVec::isOptional
            }

            either {
                expect(Token.Vec)
                expect(IDLType) storeIn IDLVec::type
            } /*or {
                expect(Token.VecDeclaration) transform {
                    var vecDeclaration = it.removeRange(0..3).trim()
                    var isOptional = false
                    if(vecDeclaration.startsWith("opt")) {
                        vecDeclaration = vecDeclaration.removeRange(0..3).trim()
                        isOptional = true
                    }
                    IDLTypeVec(
                        isOptional = isOptional,
                        vecDeclaration = vecDeclaration
                    ) as IDLType
                } storeIn IDLVec::type
            }*/
        }

        /**
         * Comment
         */
        IDLComment {
            either {
                expect(IDLSingleLineComment) storeIn self()
            }
        }
        IDLSingleLineComment {
            repeated(min = 1) {
                expect(Token.SingleLineComment) transform { it.removeRange(0..2).trim() } storeIn item
            } storeIn IDLSingleLineComment::commentLines
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
            }
        }

        IDLTypeBlob {
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeBlob::isOptional
            }
            expect(Token.Blob)
        }

        IDLTypeText {
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeText::isOptional
            }
            expect(Token.Text)
        }

        IDLTypeCustom {
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeCustom::isOptional
            }
            expect(Token.Id) storeIn IDLTypeCustom::typeDef
        }

        /**
         * Type Int
         */
        IDLTypeInt {
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
                expect(Token.Opt)
                emit(true) storeIn IDLTypeNat::isOptional
            }
            expect(Token.Nat)
        }

        IDLTypeNat64 {
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeNat64::isOptional
            }
            expect(Token.Nat64)
        }
    }

    fun parseVec(input: String): IDLVec {
        return vecParser.parse(vecLexer.tokenize(input))
    }
}