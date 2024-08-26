package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.FunType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.IDLFunArg
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
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

        @JvmStatic
        private fun funcDeclaration() = listOf(

            Arguments.of(
                "func () -> ()",
                IDLFun()
            ),

            Arguments.of(
                "func (text) -> (text)",
                IDLFun(
                    inputParams = listOf(
                        IDLFunArg(
                            idlType = IDLTypeText()
                        )
                    ),
                    outputParams = listOf(
                        IDLFunArg(
                            idlType = IDLTypeText()
                        )
                    )
                )
            ),

            Arguments.of(
                "func (dividend : nat, divisor : nat) -> (div : nat, mod : nat);",
                IDLFun(
                    inputParams = listOf(
                        IDLFunArg(
                            argId = "dividend",
                            idlType = IDLTypeNat()
                        ),
                        IDLFunArg(
                            argId = "divisor",
                            idlType = IDLTypeNat()
                        )
                    ),
                    outputParams = listOf(
                        IDLFunArg(
                            argId = "div",
                            idlType = IDLTypeNat()
                        ),
                        IDLFunArg(
                            argId = "mod",
                            idlType = IDLTypeNat()
                        )
                    )
                )
            ),

            Arguments.of(
                "func () -> (int) query",
                IDLFun(
                    funType = FunType.Query,
                    outputParams = listOf(
                        IDLFunArg(
                            idlType = IDLTypeInt()
                        )
                    )
                )
            ),

            Arguments.of(
                "func (func (int) -> ()) -> ()",
                IDLFun(
                    inputParams = listOf(
                        IDLFunArg(
                            idlType = IDLFun(
                                inputParams = listOf(
                                    IDLFunArg(
                                        idlType = IDLTypeInt()
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}