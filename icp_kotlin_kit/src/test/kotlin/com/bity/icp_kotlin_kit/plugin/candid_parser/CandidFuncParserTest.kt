package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_func.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class CandidFuncParserTest {

    @ParameterizedTest
    @MethodSource("funcDeclaration")
    internal fun parseFunc(
        funcDeclaration: String,
        expectedResult: IDLFun
    ) {
        assertEquals(
            expectedResult,
            CandidFuncParser.parseFunc(funcDeclaration)
        )
    }

    companion object {

        /**
         *
         *
         * func (dividend : nat, divisor : nat) -> (div : nat, mod : nat);
         * func () -> (int) query
         * func (func (int) -> ()) -> ()
         */
        @JvmStatic
        private fun funcDeclaration() = listOf(

            Arguments.of(
                "func () -> ()",
                IDLFun()
            ),

            Arguments.of(
                "func (text) -> (text)",
                IDLFun(
                    inputParams = listOf(IDLTypeText()),
                    outputParams = listOf(IDLTypeText())
                )
            )
        )
    }
}