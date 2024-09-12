package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFuncDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
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

internal object CandidTypeParser {

    private val typeLexer = niwenLexer {
        state {
            matches("//.*") isToken Token.SingleLineComment
            "/*" isToken Token.StartComment
            "=" isToken Token.Equals
            "(" isToken Token.LParen
            ")" isToken Token.RParen
            "{" isToken Token.LBrace
            "}" isToken Token.RBrace
            ";" isToken Token.Semi
            "," isToken Token.Comma
            "." isToken Token.Dot
            ":" isToken Token.Colon
            "->" isToken Token.Arrow
            "null" isToken Token.Null
            "text" isToken Token.Text
            "record" isToken Token.Record

            matches ("vec [^;]+") isToken Token.Vec
            matches("variant\\s*\\{[^{}]*+(?:\\{[^{}]*+}[^{}]*+)*}") isToken Token.Variant
            matches("func \\([^}]+\\)( query)?") isToken Token.Func

            "service" isToken Token.Service
            "oneway" isToken Token.Oneway
            "query" isToken Token.Query
            "composite_query" isToken Token.CompositeQuery
            "blob" isToken Token.Blob
            "type" isToken Token.Type
            "import" isToken Token.Import
            "opt" isToken Token.Opt
            "==" isToken Token.TestEqual
            "!=" isToken Token.NotEqual
            "!:" isToken Token.NotDecode
            "principal" isToken Token.Principal

            "int" isToken Token.Int

            "nat64" isToken Token.Nat64
            "nat" isToken Token.Nat

            matches("true|false") isToken Token.Boolean
            matches("[+-]") isToken Token.Sign


            matches("0[xX][0-9a-fA-F][_0-9a-fA-F]*") isToken Token.Hex
            matches("[0-9][_0-9]*") isToken Token.Decimal
            matches("[0-9]*\\.[0-9]*") isToken Token.Float
            matches("[0-9]+(\\.[0-9]*)?[eE][+-]?[0-9]+") isToken Token.Float
            matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id

            // matches("\".*? ?\"") isToken Token.String
            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val typeParser = niwenParser {

        IDLTypeDeclaration root {

            optional {
                expect(IDLComment) storeIn IDLTypeDeclaration::comment
            }

            expect(Token.Type)
            expect(Token.Id) storeIn IDLTypeDeclaration::id
            expect(Token.Equals)
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeDeclaration::isOptional
            }
            expect(IDLType) storeIn IDLTypeDeclaration::type
            expect(Token.Semi)
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
            } or {
                expect(IDLTypeFuncDeclaration) storeIn self()
            } or {
                expect(IDLTypeVariant) storeIn self()
            } or {
                expect(IDLTypeVec) storeIn self()
            } or {
                expect(IDLRecord) storeIn self()
            }
        }

        IDLTypeBoolean {
            optional {
                expect(IDLComment) storeIn IDLTypeBoolean::comment
            }
            optional {
                expect(Token.Id) storeIn IDLTypeBoolean::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeBoolean::isOptional
            }
            expect(Token.Boolean)
            optional {
                expect(Token.Semi)
            }
            optional {
                expect(IDLComment) storeIn IDLTypeBoolean::comment
            }
        }

        IDLTypeBlob {
            optional {
                expect(IDLComment) storeIn IDLTypeBlob::comment
            }
            optional {
                expect(Token.Id) storeIn IDLTypeBlob::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeBlob::isOptional
            }
            expect(Token.Blob)
            optional {
                expect(Token.Semi)
            }
            optional {
                expect(IDLComment) storeIn IDLTypeBlob::comment
            }
        }

        IDLTypeText {
            optional {
                expect(IDLComment) storeIn IDLTypeText::comment
            }
            optional {
                expect(Token.Id) storeIn IDLTypeText::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeText::isOptional
            }
            expect(Token.Text)
            optional {
                expect(Token.Semi)
            }
            optional {
                expect(IDLComment) storeIn IDLTypeText::comment
            }
        }

        IDLTypePrincipal {
            optional {
                expect(IDLComment) storeIn IDLTypePrincipal::comment
            }
            optional {
                expect(Token.Id) storeIn IDLTypePrincipal::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypePrincipal::isOptional
            }
            expect(Token.Principal)
            optional {
                expect(Token.Semi)
            }
            optional {
                expect(IDLComment) storeIn IDLTypePrincipal::comment
            }
        }

        IDLTypeCustom {
            optional {
                expect(IDLComment) storeIn IDLTypeCustom::comment
            }
            optional {
                expect(Token.Id) storeIn IDLTypeCustom::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeCustom::isOptional
            }
            expect(Token.Id) storeIn IDLTypeCustom::typeDef
            optional {
                expect(Token.Semi)
            }
            optional {
                expect(IDLComment) storeIn IDLTypeCustom::comment
            }
        }

        IDLTypeVec {
            optional {
                expect(IDLComment) storeIn IDLTypeVec::comment
            }
            optional {
                expect(Token.Id) storeIn IDLTypeVec::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeVec::isOptional
            }
            expect(Token.Vec) storeIn IDLTypeVec::vecDeclaration
            optional {
                expect(Token.Semi)
            }
            optional {
                expect(IDLComment) storeIn IDLTypeVec::comment
            }
        }

        /**
         * Type Int
         */
        IDLTypeInt {
            optional {
                expect(IDLComment) storeIn IDLTypeInt::comment
            }
            optional {
                expect(Token.Id) storeIn IDLTypeInt::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeInt::isOptional
            }
            expect(Token.Int)
            optional {
                expect(Token.Semi)
            }
            optional {
                expect(IDLComment) storeIn IDLTypeInt::comment
            }
        }

        /**
         * Type Nat
         */
        IDLTypeNat {
            optional {
                expect(IDLComment) storeIn IDLTypeNat::comment
            }
            optional {
                expect(Token.Id) storeIn IDLTypeNat::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeNat::isOptional
            }
            expect(Token.Nat)
            optional {
                expect(Token.Semi)
            }
            optional {
                expect(IDLComment) storeIn IDLTypeNat::comment
            }
        }

        IDLTypeNat64 {
            optional {
                expect(IDLComment) storeIn IDLTypeNat64::comment
            }
            optional {
                expect(Token.Id) storeIn IDLTypeNat64::id
                expect(Token.Colon)
            }
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLTypeNat64::isOptional
            }
            expect(Token.Nat64)
            optional {
                expect(Token.Semi)
            }
            optional {
                expect(IDLComment) storeIn IDLTypeNat64::comment
            }
        }

        IDLRecord {

            optional {
                expect(IDLComment) storeIn IDLRecord::comment
            }

            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLRecord::isOptional
            }

            expect(Token.Record)
            expect(Token.LBrace)
            repeated {
                expect(IDLType) storeIn item
            } storeIn IDLRecord::types
            expect(Token.RBrace)
            optional {
                expect(IDLComment) storeIn IDLRecord::comment
            }
        }
    }

    fun parseType(input: String): IDLTypeDeclaration {
        CandidFileParser.debug(typeLexer, input)
        return typeParser.parse(typeLexer.tokenize(input))
    }
}