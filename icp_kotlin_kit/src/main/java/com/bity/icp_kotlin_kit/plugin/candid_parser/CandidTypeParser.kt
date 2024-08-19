package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLTypeDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFunc
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
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
            expect(Token.Type)
            expect(IDLType) storeIn IDLTypeDeclaration::type
        }

        IDLType {
            either {
                expect(IDLTypeCustom) storeIn self()
            } or {
                expect(IDLTypeBlob) storeIn self()
            } or {
                expect(IDLTypeNat64) storeIn self()
            } or {
                expect(IDLTypeFunc) storeIn self()
            } or {
                expect(IDLTypeRecord) storeIn self()
            } or {
                expect(IDLTypeVariant) storeIn self()
            }
        }

        IDLTypeCustom {
            expect(Token.Id) storeIn IDLTypeCustom::typeId
            either {
                expect(Token.Colon)
            } or {
                expect(Token.Equals)
            }
            expect(Token.Id) storeIn IDLTypeCustom::typeDef
            expect(Token.Semi)
        }

        IDLTypeBlob {
            expect(Token.Id) storeIn IDLTypeBlob::typeId
            either {
                expect(Token.Colon)
            } or {
                expect(Token.Equals)
            }
            expect(Token.Blob)
            expect(Token.Semi)
        }

        IDLTypeNat64 {
            expect(Token.Id) storeIn IDLTypeNat64::typeId
            either {
                expect(Token.Colon)
            } or {
                expect(Token.Equals)
            }
            expect(Token.Nat64)
            expect(Token.Semi)
        }

        IDLTypeFunc {
            expect(Token.Id) storeIn IDLTypeFunc::typeId
            expect(Token.Equals)
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
            expect(Token.Semi)
        }

        IDLTypeRecord {
            expect(Token.Id) storeIn IDLTypeRecord::typeId

            either {
                expect(Token.Equals)
            } or {
                expect(Token.Colon)
            }

            expect(Token.Record)
            expect(Token.LBrace)
            repeated {
                expect(IDLType) storeIn item
            } storeIn IDLTypeRecord::records
            expect(Token.RBrace)
            expect(Token.Semi)
        }

        IDLTypeVariant {
            expect(Token.Id) storeIn IDLTypeVariant::typeId
            expect(Token.Equals)
            expect(Token.Variant)
            expect(Token.LBrace)
            repeated {
                expect(IDLType) storeIn item
            } storeIn IDLTypeVariant::types
            expect(Token.RBrace)
            expect(Token.Semi)
        }
    }

    fun parseType(input: String): IDLTypeDeclaration {
        CandidParser.debug(input)
        return typeParser.parse(lexer.tokenize(input))
    }
}