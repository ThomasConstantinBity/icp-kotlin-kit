package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileType
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.trimCommentLine
import guru.zoroark.tegral.niwen.lexer.Lexer
import guru.zoroark.tegral.niwen.lexer.matchers.matches
import guru.zoroark.tegral.niwen.lexer.niwenLexer
import guru.zoroark.tegral.niwen.parser.dsl.either
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

            "import" isToken Token.Import

            // We need to match all the type definitions including function
            matches("""type\s+\w+\s*=\s*[\w\s]+(\((\w+(, \w+)*)?\) -> \((\w+(, \w+)*)\)\s*\w*|\{[^{}]*+(?:\{[^{}]*+}[^{}]*+)*})?;""") isToken Token.Type
            matches("""service\s*:\s*\{(?:[^{}]*\{[^{}]*\})*[^{}]*\}""") isToken Token.Service

            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val fileParser = niwenParser {
        IDLFileDeclaration root {

            // TODO, need to add imports

            optional { expect(IDLComment) storeIn IDLFileDeclaration::comment }

            repeated<IDLFileDeclaration, IDLFileType> {
                expect(IDLFileType) storeIn item
            } storeIn IDLFileDeclaration::types

            optional {
                expect(IDLFileService) storeIn IDLFileDeclaration::service
            }
        }

        IDLFileType {
            optional {
                expect(IDLComment) storeIn IDLFileType::comment
            }
            expect(Token.Type) storeIn IDLFileType::typeDefinition
        }

        IDLFileService {
            optional {
                expect(IDLComment) storeIn IDLFileService::comment
            }
            expect(Token.Service) storeIn IDLFileService::serviceDefinition
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