package com.bity.icp_kotlin_kit.candid.model

internal class CandidFunctionSignature(
    val arguments: List<CandidFunctionSignatureParameter>,
    val results: List<CandidFunctionSignatureParameter>,
    val annotations: CandidFunctionSignatureAnnotation
) {
    constructor(
        inputs: List<CandidType>,
        outputs: List<CandidType>,
        query: Boolean = false,
        oneWay: Boolean = false,
        compositeQuery: Boolean = false
    ): this (
        arguments = inputs.mapIndexed { index, element ->
            CandidFunctionSignatureParameter(
                index = index,
                name = null,
                type = element
            ) },
        results = outputs.mapIndexed { index, element ->
            CandidFunctionSignatureParameter(
                index = index,
                name = null,
                type = element
            ) },
        annotations = CandidFunctionSignatureAnnotation(
            isQuery = query,
            isOneWay = oneWay,
            isCompositeQuery = compositeQuery
        )
    )
}