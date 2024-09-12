package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_vec.IDLVec
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class CandidVecParserTest {

    @MethodSource("vecDeclarationWithId")
    @ParameterizedTest
    fun `vec declaration with isd`(
        input: String,
        expectedResult: IDLVec
    ) {
        val idlVec = CandidVecParser.parseVec(input)
        assertEquals(expectedResult, idlVec)
    }

    companion object {

        @JvmStatic
        private fun vecDeclarationWithId() = listOf(

            Arguments.of(
                "vec opt Account",
                IDLVec(
                    type = TODO()
                )
            ),

            Arguments.of(
                "token_ids : vec nat",
                IDLVec(
                    id = "token_ids",
                    type = IDLTypeNat()
                )
            ),

            Arguments.of(
                "vec opt vec record { text; Value }",
                IDLVec(
                    type = TODO()
                )
            )
        )
    }
}