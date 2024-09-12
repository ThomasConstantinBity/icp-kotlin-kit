package com.bity.icp_kotlin_kit.plugin.candid_parser.util

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceParam
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class CandidServiceParamParserTest {

    @MethodSource("serviceParam")
    @ParameterizedTest
    fun `simple service param`(
        input: String,
        expectedResult: IDLServiceParam
    ) {
        val fileDeclaration = CandidServiceParamParser.parseServiceParam(input)
        assertEquals(expectedResult, fileDeclaration)
    }

    @MethodSource("serviceParamWithIds")
    @ParameterizedTest
    fun `service param with isd`(
        input: String,
        expectedResult: IDLServiceParam
    ) {
        val fileDeclaration = CandidServiceParamParser.parseServiceParam(input)
        assertEquals(expectedResult, fileDeclaration)
    }

    companion object {

        @JvmStatic
        private fun serviceParam() = listOf(

            Arguments.of(
                "(vec record { nat; opt record { text; Value } })",
                IDLServiceParam(
                    params = listOf(
                        TODO()
                    )
                )
            ),

            Arguments.of(
                "(vec TransferArg)",
                IDLServiceParam(
                    params = listOf(
                        TODO()
                    )
                )
            ),

            Arguments.of(
                "(vec opt TransferResult)",
                IDLServiceParam(
                    params = listOf(
                        TODO()
                    )
                )
            ),

            Arguments.of(
                "(vec opt vec record { text; Value })",
                IDLServiceParam(
                    params = listOf(
                        TODO()
                    )
                )
            )
        )

        @JvmStatic
        private fun serviceParamWithIds() = listOf(

            Arguments.of(
                "(token_ids : vec nat)",
                IDLServiceParam(
                    params = listOf(
                        TODO()
                    )
                )
            ),

            Arguments.of(
                "(account : Account, prev : opt nat, take : opt nat)",
                IDLServiceParam(
                    params = listOf(
                        TODO(),
                        IDLTypeNat(
                            id = "prev",
                            isOptional = true
                        ),
                        IDLTypeNat(
                            id = "take",
                            isOptional = true
                        )
                    )
                )
            )
        )
    }
}