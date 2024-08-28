package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_vec.IDLVec
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

internal object CandidVecParser {

    private val vecLexer = niwenLexer {
        state {
            matches("//.*") isToken Token.SingleLineComment

            "text" isToken Token.Text

            matches ("vec") isToken Token.Vec
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
                expect(Token.Opt)
                emit(true) storeIn IDLVec::isOptional
            }
            expect(Token.Vec)
            expect(IDLType) storeIn IDLVec::type
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
        IDLTypeRecord { expect(Token.Record) storeIn IDLTypeRecord::recordDeclaration }
    }

    fun parseVec(input: String): IDLVec {
        return vecParser.parse(vecLexer.tokenize(input))
    }
}