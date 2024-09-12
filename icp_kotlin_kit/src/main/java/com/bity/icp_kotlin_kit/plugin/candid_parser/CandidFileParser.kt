package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.trimCommentLine
import guru.zoroark.tegral.niwen.lexer.Lexer
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

internal object CandidFileParser {

    private val fileLexer = niwenLexer {
        state {
            matches("//.*") isToken Token.SingleLineComment

            "{" isToken Token.LBrace
            "}" isToken Token.RBrace
            ":" isToken Token.Colon
            ";" isToken Token.Semi
            "->" isToken Token.Arrow
            "=" isToken Token.Equals

            "opt" isToken Token.Opt

            "import" isToken Token.Import

            "type" isToken  Token.Type
            "service" isToken Token.Service

            "vec" isToken Token.Vec
            "record" isToken Token.Record
            "variant" isToken Token.Variant

            "null" isToken Token.Null
            "blob" isToken Token.Blob
            "nat64" isToken Token.Nat64

            matches("""\bquery\b(?!_)""") isToken Token.Query
            matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id

            // TODO replace
            matches("""\((?:[^()]*|\((?:[^()]*|\([^()]*\))*\))*\)""") isToken Token.ServiceArgs

            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val fileParser = niwenParser {
        IDLFileDeclaration root {

            // TODO, need to add imports

            optional {
                expect(IDLComment) storeIn IDLFileDeclaration::comment
            }

            optional {
                repeated<IDLFileDeclaration, IDLType>(min = 1) {
                    expect(Token.Type)
                    expect(IDLType) storeIn item
                } storeIn IDLFileDeclaration::types
            }

            optional {
                expect(Token.Service)
                expect(Token.Colon)
                expect(Token.LBrace)
                repeated<IDLFileDeclaration, IDLService>(min = 1) {
                    expect(IDLService) storeIn item
                } storeIn IDLFileDeclaration::services
                expect(Token.RBrace)
            }
        }

        IDLService {
            optional {
                expect(IDLComment) storeIn IDLService::comment
            }
            expect(Token.Id) storeIn IDLService::id
            expect(Token.Colon)
            expect(Token.ServiceArgs) storeIn IDLService::inputParamsDeclaration
            expect(Token.Arrow)
            expect(Token.ServiceArgs) storeIn IDLService::outputParamsDeclaration
            optional {
                expect(Token.Query)
                emit(IDLServiceType.Query) storeIn IDLService::serviceType
            }
            expect(Token.Semi)
        }

        /**
         * Comment
         */
        IDLComment {
            // TODO, add different comment support
            either {
                expect(IDLSingleLineComment) storeIn self()
            }
        }
        IDLSingleLineComment {
            repeated(min = 1) {
                expect(Token.SingleLineComment) transform { it.trimCommentLine() } storeIn item
            } storeIn IDLSingleLineComment::commentLines
        }

        IDLType {
            either {
                expect(IDLRecord) storeIn self()
            } or {
                expect(IDLTypeNat64) storeIn self()
            } or {
                expect(IDLTypeBlob) storeIn self()
            } or {
                expect(IDLTypeCustom) storeIn self()
            } or {
                expect(IDLTypeVariant) storeIn self()
            } or {
                expect(IDLTypeVec) storeIn self()
            } or {
                expect(IDLTypeNull) storeIn self()
            }
        }

        IDLRecord {

            optional {
                expect(IDLComment) storeIn IDLRecord::comment
            }

            expect(Token.Id) storeIn IDLRecord::recordName

            either {
                expect(Token.Equals)
                expect(Token.Record)
                expect(Token.LBrace)
            } or {
                expect(Token.Colon)
                expect(Token.Record)
                expect(Token.LBrace)
            }
            repeated(min = 1) {
                expect(IDLType) storeIn item
                optional { expect(Token.Semi) }
            } storeIn IDLRecord::types
            expect(Token.RBrace)
            optional {
                expect(Token.Semi)
            }
        }

        IDLTypeVariant {
            expect(Token.Id) storeIn IDLTypeVariant::variantDeclaration
            expect(Token.Equals)
            expect(Token.Variant)
            expect(Token.LBrace)
            repeated(min = 1) {
                expect(IDLType) storeIn item
            } storeIn IDLTypeVariant::types
            expect(Token.RBrace)
            expect(Token.Semi)
        }

        IDLTypeCustom {
            optional {
                expect(IDLComment) storeIn IDLTypeCustom::comment
            }
            either {
                expect(Token.Id) storeIn IDLTypeCustom::typeDef
                expect(Token.Equals)
                expect(IDLType) storeIn IDLTypeCustom::type
            } or {
                expect(Token.Id) storeIn IDLTypeCustom::id
                expect(Token.Colon)
                optional {
                    expect(Token.Opt)
                    emit(true) storeIn IDLTypeCustom::isOptional
                }
                expect(Token.Id) storeIn IDLTypeCustom::typeDef
            } or {
                expect(Token.Id) storeIn IDLTypeCustom::typeDef
            }
            expect(Token.Semi)
        }

        IDLTypeBlob {
            expect(Token.Blob)
        }

        IDLTypeNat64 {
            optional {
                expect(IDLComment) storeIn IDLTypeNat64::comment
            }
            either {
                expect(Token.Id) storeIn IDLTypeNat64::id
                expect(Token.Colon)
                expect(Token.Nat64)
                optional {
                    expect(Token.Semi)
                }
            } or {
                expect(Token.Nat64)
            }
        }

        IDLTypeVec {
            either {
                expect(Token.Id) storeIn IDLTypeVec::vecDeclaration
                expect(Token.Equals)
                expect(Token.Vec)
                expect(IDLType) storeIn IDLTypeVec::vecType
            } or {
                expect(Token.Id) storeIn IDLTypeVec::id
                expect(Token.Colon)
                expect(Token.Vec)
                expect(IDLType) storeIn IDLTypeVec::vecType
            }
        }

        IDLTypeNull {
            expect(Token.Id) storeIn IDLTypeNull::nullDefinition
            expect(Token.Colon)
            expect(Token.Null)
            expect(Token.Semi)
        }
    }

    fun parseFile(input: String): IDLFileDeclaration {
        debug(fileLexer, input)
        return fileParser.parse(fileLexer.tokenize(input))
    }

    // TODO delete
    fun debug(lexer: Lexer, input: String) {
        println(input)
        lexer.tokenize(input).forEachIndexed { i, t ->
            println("[$i] - ${t.tokenType} '${t.string}'")

        }
    }
}