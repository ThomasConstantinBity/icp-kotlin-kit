package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFunc
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

internal class CandidTypeParserTest {

    @ParameterizedTest
    @MethodSource("singleTypeDeclaration")
    fun parseTypeTest (
        input: String,
        expectedResult: IDLType
    ) {
        val typeDeclaration = CandidTypeParser.parseType(input)
        assertEquals(expectedResult, typeDeclaration.type)
    }

    companion object {

        @JvmStatic
        private fun singleTypeDeclaration() = listOf(
            Arguments.of(
                "type AccountIdentifier = blob;",
                IDLTypeBlob("AccountIdentifier")
            ),

            Arguments.of(
                "type Memo = nat64;",
                IDLTypeNat64("Memo")
            ),

            Arguments.of(
                "type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult) query;",
                IDLTypeFunc(
                    typeId = "QueryArchiveFn",
                    inputParams = listOf("GetBlocksArgs"),
                    outputParams = listOf("QueryArchiveResult"),
                    funcType = "query"
                )
            ),

            Arguments.of(
                "type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult);",
                IDLTypeFunc(
                    typeId = "QueryArchiveFn",
                    inputParams = listOf("GetBlocksArgs"),
                    outputParams = listOf("QueryArchiveResult"),
                    funcType = null
                )
            ),

            Arguments.of(
                """
                    type Tokens = record {
                        e8s : nat64;
                    };
                """.trimIndent(),
                IDLTypeRecord(
                    "Tokens",
                    listOf(
                        IDLTypeNat64("e8s")
                    )
                )
            ),

            Arguments.of(
                """
                    type Transfer = variant {
                        Mint: record {
                            to: AccountIdentifier;
                            amount: Tokens;
                        };
                        Burn: record {
                             from: AccountIdentifier;
                             amount: Tokens;
                       };
                        Send: record {
                            from: AccountIdentifier;
                            to: AccountIdentifier;
                            amount: Tokens;
                        };
                    };
                """.trimIndent(),
                IDLTypeVariant(
                    typeId = "Transfer",
                    types = listOf(
                        IDLTypeRecord(
                            typeId =  "Mint",
                            records = listOf(
                                IDLTypeCustom(
                                    typeId = "to",
                                    typeDef = "AccountIdentifier"
                                ),
                                IDLTypeCustom(
                                    typeId = "amount",
                                    typeDef = "Tokens"
                                ),
                            )
                        ),
                        IDLTypeRecord(
                            typeId =  "Burn",
                            records = listOf(
                                IDLTypeCustom(
                                    typeId = "from",
                                    typeDef = "AccountIdentifier"
                                ),
                                IDLTypeCustom(
                                    typeId = "amount",
                                    typeDef = "Tokens"
                                ),
                            )
                        ),
                        IDLTypeRecord(
                            typeId =  "Send",
                            records = listOf(
                                IDLTypeCustom(
                                    typeId = "from",
                                    typeDef = "AccountIdentifier"
                                ),
                                IDLTypeCustom(
                                    typeId = "to",
                                    typeDef = "AccountIdentifier"
                                ),
                                IDLTypeCustom(
                                    typeId = "amount",
                                    typeDef = "Tokens"
                                ),
                            )
                        )
                    )
                )
            )
        )
    }
}