package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFunc
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class CandidTypeParserTest {

    @ParameterizedTest
    @MethodSource("singleTypeDeclaration")
    fun `single type declaration`(
        input: String,
        expectedResult: IDLType
    ) {
        val typeDeclaration = CandidTypeParser.parseType(input)
        assertEquals(expectedResult, typeDeclaration.type)
    }

    @Test
    fun `type func`() {
        val input = "type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult) query;"
        val typeDeclaration = CandidTypeParser.parseType(input)
        val idlTypeFunc = typeDeclaration.type
        assertTrue(idlTypeFunc is IDLTypeFunc)
        assertEquals("QueryArchiveFn", idlTypeFunc.typeId)
        assertEquals("GetBlocksArgs", idlTypeFunc.inputParams.joinToString())
        assertEquals("QueryArchiveResult", idlTypeFunc.outputParams.joinToString())
        assertEquals("query", idlTypeFunc.funcType)
    }

    @Test
    fun `type func without func type`() {
        val input = "type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult);"
        val typeDeclaration = CandidTypeParser.parseType(input)
        val idlTypeFunc = typeDeclaration.type
        assertTrue(idlTypeFunc is IDLTypeFunc)
        assertEquals("QueryArchiveFn", idlTypeFunc.typeId)
        assertEquals("GetBlocksArgs", idlTypeFunc.inputParams.joinToString())
        assertEquals("QueryArchiveResult", idlTypeFunc.outputParams.joinToString())
        assertNull(idlTypeFunc.funcType)
    }

    @Test
    fun `type record`() {
        val input = """
            type Tokens = record {
                 e8s : nat64;
            };
        """.trimIndent()
        val typeDeclaration = CandidTypeParser.parseType(input)
        val type = typeDeclaration.type
        assertTrue(type is IDLTypeRecord)
        assertEquals("Tokens", type.typeId)
        val recordTypeList = type.records
        assertEquals(1, recordTypeList.size)
        val firstRecordType = recordTypeList.first()
        assertEquals(IDLTypeNat64("e8s"), firstRecordType)
    }

    @Test
    fun `type variant`() {
        val input = """
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
        """.trimIndent()
        val typeDeclaration = CandidTypeParser.parseType(input)

        val typeVariant = typeDeclaration.type
        assertTrue(typeVariant is IDLTypeVariant)
        assertEquals("Transfer", typeVariant.typeId)

        val types = typeVariant.types
        assertEquals(3, types.size)

        val mint = types.first()
        assertEquals(
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
            mint
        )

        val burn = types[1]
        assertEquals(
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
            burn
        )

        val send = types.last()
        assertEquals(
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
            ),
            send
        )
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
            )
        )
    }
}