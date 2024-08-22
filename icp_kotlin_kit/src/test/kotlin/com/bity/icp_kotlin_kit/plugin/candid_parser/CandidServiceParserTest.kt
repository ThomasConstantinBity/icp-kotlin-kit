package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceDeclaration
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

internal class CandidServiceParserTest {

    @MethodSource("services")
    @ParameterizedTest(name = "[{index}] - {0}")
    fun `parse service`(
        input: String,
        expectedResult: IDLServiceDeclaration
    ) {
        val idlServiceDeclaration = CandidServiceParser.parseService(input)
        assertEquals(expectedResult, idlServiceDeclaration)
    }

    companion object {

        @JvmStatic
        private fun services() = listOf(

            Arguments.of(
                "service : {}",
                IDLServiceDeclaration()
            ),

            Arguments.of(
                """
                    service : {
                      ping : () -> ();
                    }
                """.trimIndent(),
                IDLServiceDeclaration(
                    services = listOf(
                        IDLService(
                            id = "ping",
                            inputParamsDeclaration = "",
                            outputParamsDeclaration = ""
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    service : {
                      reverse : (text) -> (text);
                      divMod : (dividend : nat, divisor : nat) -> (div : nat, mod : nat);
                    }
                """.trimIndent(),
                IDLServiceDeclaration(
                    services = listOf(
                        IDLService(
                            id = "reverse",
                            inputParamsDeclaration = "text",
                            outputParamsDeclaration = "text"
                        ),
                        IDLService(
                            id = "divMod",
                            inputParamsDeclaration = "dividend : nat, divisor : nat",
                            outputParamsDeclaration = "div : nat, mod : nat"
                        )
                    )
                )
            ),

            Arguments.of(
                """
                    service : (InitArgs) -> {
                        authorize : (principal, Auth) -> (success : bool);
                        eth_getTransactionCount : (RpcServices, opt RpcConfig, GetTransactionCountArgs) -> (
                        MultiGetTransactionCountResult
                        );
                    }
                """.trimIndent(),
                IDLServiceDeclaration(
                    initArgsDeclaration = "InitArgs",
                    services = listOf(
                        IDLService(
                            id = "authorize",
                            inputParamsDeclaration = "principal, Auth",
                            outputParamsDeclaration = "success : bool"
                        ),
                        IDLService(
                            id = "eth_getTransactionCount",
                            inputParamsDeclaration = "RpcServices, opt RpcConfig, GetTransactionCountArgs",
                            outputParamsDeclaration = "MultiGetTransactionCountResult"
                        )
                    )
                )
            )
        )
    }
}