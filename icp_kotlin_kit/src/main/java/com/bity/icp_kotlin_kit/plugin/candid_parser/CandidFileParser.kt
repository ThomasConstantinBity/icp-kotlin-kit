package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceType
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
            "import" isToken Token.Import
            "service" isToken Token.Service

            // We need to match all the type definitions including function
            matches("""type\s+\w+\s*=\s*[\w\s]+(\((\w+(, \w+)*)?\) -> \((\w+(, \w+)*)\)\s*\w*|\{[^{}]*+(?:\{[^{}]*+}[^{}]*+)*})?;""") isToken Token.Type

            matches("""\bquery\b(?!_)""") isToken Token.Query
            matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id
            matches("""\((?:[^()]*|\((?:[^()]*|\([^()]*\))*\))*\)""") isToken Token.ServiceArgs

            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val fileParser = niwenParser {
        IDLFileDeclaration root {

            // TODO, need to add imports

            optional { expect(IDLComment) storeIn IDLFileDeclaration::comment }

            optional {
                repeated<IDLFileDeclaration, IDLFileType>(min = 1) {
                    expect(IDLFileType) storeIn item
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

        IDLFileType {
            optional {
                expect(IDLComment) storeIn IDLFileType::comment
            }
            expect(Token.Type) storeIn IDLFileType::typeDefinition
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
    }

    fun parseFile(input: String): IDLFileDeclaration {
        // debug(fileLexer, input)
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