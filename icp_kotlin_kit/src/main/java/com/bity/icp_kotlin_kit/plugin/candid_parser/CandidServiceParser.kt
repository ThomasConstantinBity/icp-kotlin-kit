package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceType
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

internal object CandidServiceParser {

    private val serviceLexer = niwenLexer {
        state {
            matches("//.*") isToken Token.SingleLineComment

            "service" isToken Token.Service
            ":" isToken Token.Colon
            "{" isToken Token.LBrace
            "}" isToken Token.RBrace
            "->" isToken Token.Arrow
            ";" isToken Token.Semi
            "->" isToken Token.Arrow

            matches("""\bquery\b(?!_)""") isToken Token.Query
            matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id
            matches("""\((?:[^()]*|\((?:[^()]*|\([^()]*\))*\))*\)""") isToken Token.ServiceArgs

            matches("[ \t\r\n]+").ignore
            matches("//[^\n]*").ignore
        }
    }

    private val serviceParser = niwenParser {

        IDLServiceDeclaration root {
            expect(Token.Service)
            expect(Token.Colon)

            optional {
                expect(Token.ServiceArgs) transform { formatParamDeclaration(it) } storeIn IDLServiceDeclaration::initArgsDeclaration
                expect(Token.Arrow)
            }

            expect(Token.LBrace)

            repeated<IDLServiceDeclaration,IDLService > {
                expect(IDLService) storeIn item
                expect(Token.Semi)
            } storeIn IDLServiceDeclaration::services

            expect(Token.RBrace)
        }

        IDLService {
            optional {
                expect(IDLComment) storeIn IDLService::comment
            }
            expect(Token.Id) storeIn IDLService::id
            expect(Token.Colon)
            expect(Token.ServiceArgs) transform { formatParamDeclaration(it) } storeIn IDLService::inputParamsDeclaration
            expect(Token.Arrow)
            expect(Token.ServiceArgs) transform { formatParamDeclaration(it) } storeIn IDLService::outputParamsDeclaration
            optional {
                either {
                    expect(Token.Query)
                    emit(IDLServiceType.Query) storeIn IDLService::serviceType
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
        IDLSingleLineComment {
            repeated(min = 1) {
                expect(Token.SingleLineComment) transform { it.removeRange(0..2).trim() } storeIn item
            } storeIn IDLSingleLineComment::commentLines
        }
    }

    fun parseService(input: String): IDLServiceDeclaration {
        return serviceParser.parse(serviceLexer.tokenize(input))
    }

    private fun formatParamDeclaration(params: String) =
        params.substring(1, params.length - 1)
            .replace("\\s+".toRegex(), " ")
            .trim()
}