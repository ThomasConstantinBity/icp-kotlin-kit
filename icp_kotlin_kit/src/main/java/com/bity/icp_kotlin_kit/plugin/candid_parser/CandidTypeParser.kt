package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLTypeDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLTypeList
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFunc
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.lexer
import guru.zoroark.tegral.niwen.parser.dsl.either
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

            // Single type
            either {
                expect(Token.Type)
                expect(Token.Id) storeIn IDLTypeDeclaration::typeId
                expect(Token.Equals)
                expect(IDLTypeList) storeIn IDLTypeDeclaration::idlTypeList
                expect(Token.Semi)
            } or {
                expect(Token.Type)
                expect(Token.Id) storeIn IDLTypeDeclaration::typeId
                expect(Token.Equals)
                // Type func
                expect(IDLTypeFunc) transform { IDLTypeList(listOf(it)) } storeIn IDLTypeDeclaration::idlTypeList
                expect(Token.Semi)
            }
        }

        IDLTypeList {
            repeated {
                expect(IDLType) storeIn item
            } storeIn IDLTypeList::types
        }

        IDLType {
            either {
                expect(IDLTypeBlob) storeIn self()
            } or {
                expect(IDLTypeNat64) storeIn self()
            } or {
                expect(IDLTypeFunc) storeIn self()
            }
        }

        IDLTypeBlob { expect(Token.Blob) }
        IDLTypeNat64 { expect(Token.Nat64) }

        IDLTypeFunc {
            expect(Token.Func)
            expect(Token.LParen)

            // TODO: Can be improved for single param and multiple param
            repeated {
                expect(Token.Id) storeIn item
                optional { expect(Token.Comma) }
            } storeIn IDLTypeFunc::inputParams
            expect(Token.RParen)

            expect(Token.Arrow)
            expect(Token.LParen)

            // TODO: Can be improved for single param and multiple param
            repeated {
                expect(Token.Id) storeIn item
                optional { expect(Token.Comma) }
            } storeIn IDLTypeFunc::outputParams
            expect(Token.RParen)

            optional {
                expect(Token.Query) storeIn IDLTypeFunc::funcType
            }
        }
    }

    fun parseType(input: String): IDLTypeDeclaration {
        CandidParser.debug(input)
        return typeParser.parse(lexer.tokenize(input))
    }
}