package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFuncDeclaration
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class KotlinFunctionGeneratorTest {

    @ParameterizedTest
    @MethodSource("funcDeclaration")
    fun kotlinFunDeclaration(
        functionId: String,
        idlTypeFuncDeclaration: IDLTypeFuncDeclaration,
        expectedResult: String
    ) {
        val kotlinFunction = KotlinFunctionGenerator(
            funId = functionId,
            idlTypeFunc = idlTypeFuncDeclaration,
            className = "LEdgerCanister"
        )
        assertEquals(expectedResult, kotlinFunction)
    }

    companion object {

        @JvmStatic
        private fun funcDeclaration() = listOf(
            Arguments.of(
                "FunctionId",
                IDLTypeFuncDeclaration("func () -> ()"),
                "typealias FunctionId = () -> Unit"
            ),

            Arguments.of(
                "FunctionId",
                IDLTypeFuncDeclaration("func (text) -> (text)"),
                "typealias FunctionId = (String) -> String"
            ),

            Arguments.of(
                "FunctionId",
                IDLTypeFuncDeclaration("func (dividend : nat, divisor : nat) -> (div : nat, mod : nat)"),
                "typealias FunctionId = (dividend: UInt, divisor: UInt) -> NTuple2(div: UInt, mod: UInt)"
            ),

            Arguments.of(
                "FunctionId",
                IDLTypeFuncDeclaration("func () -> (int) query"),
                "typealias FunctionId = () -> Int"
            ),

            Arguments.of(
                "FunctionId",
                IDLTypeFuncDeclaration("func (func (int) -> ()) -> ()"),
                "typealias FunctionId = ((Int) -> Unit) -> Unit"
            )
        )
    }
}