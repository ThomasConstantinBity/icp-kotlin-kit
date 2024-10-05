package com.bity.icp_kotlin_kit.candid.model

internal sealed class FunctionSignatureType {

    class Concrete(
        val candidFunctionSignature: CandidFunctionSignature
    ): FunctionSignatureType()

    class Reference(val string: String): FunctionSignatureType()
}