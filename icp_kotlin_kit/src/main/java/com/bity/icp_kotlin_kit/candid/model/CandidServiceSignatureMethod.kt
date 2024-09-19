package com.bity.icp_kotlin_kit.candid.model

internal class CandidServiceSignatureMethod(
    val name: String,
    val functionSignature: FunctionSignatureType
) {
    constructor(
        name: String,
        functionSignature: CandidFunctionSignature
    ): this(
        name = name,
        functionSignature = FunctionSignatureType.Concrete(functionSignature)
    )
}