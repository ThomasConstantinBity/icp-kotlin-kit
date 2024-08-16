package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLTypeDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLTypeList
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFunc
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
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
        expectedResult: IDLTypeDeclaration
    ) {
        val typeDeclaration = CandidTypeParser.parseType(input)
        assertEquals(
            expected = expectedResult,
            actual = typeDeclaration
        )
    }

    @Test
    fun `type func declaration`() {
        val input = "type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult) query;"
        val typeDeclaration = CandidTypeParser.parseType(input)
        assertEquals("QueryArchiveFn", typeDeclaration.typeId)
        val func = typeDeclaration.idlTypeList.types.first()
        assertTrue(func is IDLTypeFunc)
        assertEquals("GetBlocksArgs", func.inputParams.joinToString())
        assertEquals("QueryArchiveResult", func.outputParams.joinToString())
        assertEquals("query", func.funcType)
    }

    @Test
    fun `type func declaration without func type`() {
        val input = "type QueryArchiveFn = func (GetBlocksArgs) -> (QueryArchiveResult);"
        val typeDeclaration = CandidTypeParser.parseType(input)
        assertEquals("QueryArchiveFn", typeDeclaration.typeId)
        val func = typeDeclaration.idlTypeList.types.first()
        assertTrue(func is IDLTypeFunc)
        assertEquals("GetBlocksArgs", func.inputParams.joinToString())
        assertEquals("QueryArchiveResult", func.outputParams.joinToString())
        assertNull(func.funcType)
    }

    companion object {

        @JvmStatic
        private fun singleTypeDeclaration() = listOf(
            Arguments.of(
                "type AccountIdentifier = blob;",
                IDLTypeDeclaration(
                    typeId = "AccountIdentifier",
                    idlTypeList = IDLTypeList(listOf(IDLTypeBlob()))
                )
            ),

            Arguments.of(
                "type Memo = nat64;",
                IDLTypeDeclaration(
                    typeId = "Memo",
                    idlTypeList = IDLTypeList(listOf(IDLTypeNat64()))
                )
            )
        )
    }
}