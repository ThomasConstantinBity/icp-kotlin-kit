package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.FunType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
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
import guru.zoroark.tegral.niwen.parser.dsl.lookahead
import guru.zoroark.tegral.niwen.parser.dsl.niwenParser
import guru.zoroark.tegral.niwen.parser.dsl.optional
import guru.zoroark.tegral.niwen.parser.dsl.or
import guru.zoroark.tegral.niwen.parser.dsl.repeated
import guru.zoroark.tegral.niwen.parser.dsl.self

// TODO, add support for end of line comment in order to support multiple comment
// type QueryArchiveResult = variant {
//    Err : null;      // we don't know the values here...
//    // A new line comment
//    Ok : BlockRange;
//
//};
internal object CandidParser {

    private val fileLexer = niwenLexer {
        state {
            matches("//.*") isToken Token.SingleLineComment

            "{" isToken Token.LBrace
            "}" isToken Token.RBrace
            "(" isToken Token.LParen
            ")" isToken Token.RParen
            ":" isToken Token.Colon
            ";" isToken Token.Semi
            "->" isToken Token.Arrow
            "=" isToken Token.Equals

            "opt" isToken Token.Opt

            "import" isToken Token.Import

            "type" isToken  Token.Type
            "service" isToken Token.Service

            "func" isToken Token.Func
            "vec" isToken Token.Vec
            "record" isToken Token.Record
            "variant" isToken Token.Variant

            "null" isToken Token.Null
            "blob" isToken Token.Blob
            "nat64" isToken Token.Nat64
            "principal" isToken Token.Principal

            matches("""\bquery\b(?!_)""") isToken Token.Query
            matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id

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
                    expect(IDLType) storeIn item
                } storeIn IDLFileDeclaration::types
            }

            optional {
                expect(Token.Service)
                expect(Token.Colon)
                expect(Token.LBrace)
                repeated<IDLFileDeclaration, IDLFun>(min = 1) {
                    expect(IDLFun) storeIn item
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
            } or {
                expect(IDLFun) storeIn self()
            } or {
                expect(IDLTypePrincipal) storeIn self()
            }
        }

        IDLRecord {

            optional {
                expect(IDLComment) storeIn IDLRecord::comment
            }

            either {
                expect(Token.Type)
                expect(Token.Id) storeIn IDLRecord::recordName
                expect(Token.Equals)
                expect(Token.Record)
                expect(Token.LBrace)
            } or {
                expect(Token.Id) storeIn IDLRecord::recordName
                expect(Token.Colon)
                expect(Token.Record)
                expect(Token.LBrace)
            } or {
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

            optional {
                expect(IDLComment) storeIn IDLTypeVariant::comment
            }

            expect(Token.Type)
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
                expect(Token.Type)
                expect(Token.Id) storeIn IDLTypeCustom::typeDef
                expect(Token.Equals)
                expect(IDLType) storeIn IDLTypeCustom::type
                optional {
                    expect (IDLComment) storeIn IDLTypeCustom::comment
                }
                expect(Token.Semi)
            } or {
                expect(Token.Id) storeIn IDLTypeCustom::id
                expect(Token.Colon)
                optional {
                    expect(Token.Opt)
                    emit(true) storeIn IDLTypeCustom::isOptional
                }
                expect(Token.Id) storeIn IDLTypeCustom::typeDef
                optional {
                    expect (IDLComment) storeIn IDLTypeCustom::comment
                }
                expect(Token.Semi)
            } or {
                expect(Token.Id) storeIn IDLTypeCustom::typeDef
                optional {
                    expect (IDLComment) storeIn IDLTypeCustom::comment
                }
                expect(Token.Semi)
            } or {
                expect(Token.Id) storeIn IDLTypeCustom::typeDef
                lookahead {
                    either {
                        expect(Token.Comma)
                    } or {
                        expect(Token.RParen)
                    }
                }
            }
        }

        IDLTypeBlob {

            optional {
                expect(IDLComment) storeIn IDLTypeBlob::comment
            }

            either {
                optional {
                    expect(Token.Opt)
                    emit(true) storeIn IDLTypeBlob::isOptional
                }
                expect(Token.Blob)
            } or {
                expect(Token.Id) storeIn IDLTypeBlob::id
                expect(Token.Colon)
                optional {
                    expect(Token.Opt)
                    emit(true) storeIn IDLTypeBlob::isOptional
                }
                expect(Token.Blob)
            }
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

            optional {
                expect(IDLComment) storeIn IDLTypeVec::comment
            }

            either {
                expect(Token.Type)
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

            optional {
                expect(IDLComment) storeIn IDLTypeNull::comment
            }

            expect(Token.Id) storeIn IDLTypeNull::nullDefinition
            expect(Token.Colon)
            expect(Token.Null)
            expect(Token.Semi)

            optional {
                expect(IDLComment) storeIn IDLTypeNull::comment
            }
        }

        IDLFun {

            optional {
                expect(IDLComment) storeIn IDLFun::comment
            }

            either {
                expect(Token.Type)
                expect(Token.Id) storeIn IDLFun::funcName
                expect(Token.Equals)
                expect(Token.Func)
            } or {
                expect(Token.Id) storeIn IDLFun::id
                expect(Token.Colon)
            }

            // Input args declaration
            expect(Token.LParen)
            repeated {
                expect(IDLType) storeIn item
            } storeIn IDLFun::inputArgs
            expect(Token.RParen)

            expect(Token.Arrow)

            // Output args declaration
            expect(Token.LParen)
            repeated {
                expect(IDLType) storeIn item
            } storeIn IDLFun::outputArgs
            expect(Token.RParen)

            optional {
                expect(Token.Query)
                emit(FunType.Query) storeIn IDLFun::funType
            }
            expect(Token.Semi)
        }

        IDLTypePrincipal {
            expect(Token.Id) storeIn IDLTypePrincipal::id
            expect(Token.Colon)
            expect(Token.Principal)
        }
    }

    fun parseFile(input: String): IDLFileDeclaration {
        // debug(input)
        return fileParser.parse(fileLexer.tokenize(input))
    }

    // TODO delete
    private fun debug(input: String) {
        println(input)
        fileLexer.tokenize(input).forEachIndexed { i, t ->
            println("[$i] - ${t.tokenType} '${t.string}'")
        }
    }
}