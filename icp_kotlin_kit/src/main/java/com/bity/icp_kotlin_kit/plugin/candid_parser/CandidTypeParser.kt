package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLTypeDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.comment.IDLComment
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
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.lexer
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

    fun parseType(input: String): IDLTypeDeclaration {
        return typeParser.parse(lexer.tokenize(input))
    }
}