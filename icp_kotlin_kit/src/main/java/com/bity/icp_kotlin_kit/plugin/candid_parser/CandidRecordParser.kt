package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_record.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_record.IDLRecordDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.trimCommentLine
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.trimEndOfLineComment
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

internal object CandidRecordParser {

    private val recordLexer = niwenLexer {
        state {
            matches(";[^\n]\\s*//.*") isToken Token.EndOfLineComment
            matches("//.*") isToken Token.SingleLineComment
            "{" isToken Token.LBrace
            "}" isToken Token.RBrace
            ";" isToken Token.Semi
            ":" isToken Token.Colon

            "opt" isToken Token.Opt

            "principal" isToken Token.Principal

            // TODO, is it possible to have nested records?
            "record" isToken Token.Record

            "text" isToken Token.Text
            "bool" isToken Token.Boolean
            "blob" isToken Token.Blob

            "int" isToken Token.Int

            "nat64" isToken Token.Nat64
            "nat" isToken Token.Nat

            // TODO
            matches("""vec\s+record\s+\{([^{}]*|\{[^{}]*\})*}""") isToken Token.Vec
            matches("""vec\s+\w+""") isToken Token.Vec

            matches("\"([a-zA-Z_][a-zA-Z0-9_]*)\"|([a-zA-Z_][a-zA-Z0-9_]*)") isToken Token.Id
            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val recordParser = niwenParser {

        IDLRecordDeclaration root{
            expect(Token.Record)
            expect(Token.LBrace)

            repeated<IDLRecordDeclaration, IDLRecord> {
                expect(IDLRecord) storeIn item
            } storeIn IDLRecordDeclaration::records

            expect(Token.RBrace)
            optional { expect(Token.Semi) }
        }

        IDLRecord {
            optional {
                expect(IDLComment) storeIn IDLRecord::comment
            }

            expect(Token.Id) storeIn IDLRecord::id
            expect(Token.Colon)
            optional {
                expect(Token.Opt)
                emit(true) storeIn IDLRecord::isOptional
            }
            expect(IDLType) storeIn IDLRecord::type

            optional {
                either {
                    expect(Token.Semi)
                } or {
                    expect(IDLComment) storeIn IDLRecord::comment
                }
            }
        }

        /**
         * Comment
         */
        IDLComment {
            either {
                expect(IDLSingleLineComment) storeIn self()
            }
        }

        // TODO, can improve adding an extra variable for endOfLineComment
        IDLSingleLineComment {
            either {
                repeated(min = 1) {
                    expect(Token.SingleLineComment) transform { it.trimCommentLine() } storeIn item
                } storeIn IDLSingleLineComment::commentLines
            } or {
                repeated(min = 1) {
                    expect(Token.EndOfLineComment) transform { it.trimEndOfLineComment() } storeIn item
                } storeIn IDLSingleLineComment::commentLines
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

    fun parseRecord(input: String): IDLRecordDeclaration {
        return recordParser.parse(recordLexer.tokenize(input))
    }
}