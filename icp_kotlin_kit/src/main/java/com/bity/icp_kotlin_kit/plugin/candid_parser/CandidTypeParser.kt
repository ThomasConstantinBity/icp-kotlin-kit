package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFunc
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
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

            matches("vec record \\{[^}]+\\}") isToken Token.VecRecord
            matches ("vec [^;]+") isToken Token.Vec
            matches("record \\{[^}]+\\}") isToken Token.Record
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
                expect(IDLTypeFunc) storeIn self()
            } or {
                expect(IDLTypeRecord) storeIn self()
            } or {
                expect(IDLTypeVariant) storeIn self()
            } or {
                expect(IDLTypeVec) storeIn self()
            }
        }

        IDLTypeBlob { expect(Token.Blob) }
        IDLTypeText { expect(Token.Text) }
        IDLTypeCustom { expect(Token.Id) storeIn IDLTypeCustom::typeDef }

        /**
         * Type Int
         */
        IDLTypeInt { expect(Token.Int) }

        /**
         * Type Nat
         */
        IDLTypeNat { expect(Token.Nat) }
        IDLTypeNat64 { expect(Token.Nat64) }

        IDLTypeVec { expect(Token.Vec) storeIn IDLTypeVec::vecDeclaration }
        IDLTypeFunc { expect(Token.Func) storeIn IDLTypeFunc::funcDeclaration }
        IDLTypeRecord { expect(Token.Record) storeIn IDLTypeRecord::recordDeclaration }
        IDLTypeVariant { expect(Token.Variant) storeIn IDLTypeVariant::variantDeclaration }
    }

    fun parseType(input: String): IDLTypeDeclaration =
        typeParser.parse(typeLexer.tokenize(input))
}