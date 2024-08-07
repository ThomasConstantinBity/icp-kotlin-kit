package com.bity.icp_kotlin_kit.candid.model

internal class CandidFunction(
    val signature: CandidFunctionSignature,
    val method: ServiceMethod?
) {
    class ServiceMethod(
        val name: String,
        val principalId: ByteArray
    )
    class CandidFunctionSignature(
        val inputs: List<CandidType>,
        val outputs: List<CandidType>,
        // indicates that the referenced function is a query method,
        // meaning it does not alter the state of its canister, and that
        // it can be invoked using the cheaper “query call” mechanism.
        val isQuery: Boolean,
        // indicates that this function returns no response, intended for fire-and-forget scenarios.
        val isOneWay: Boolean
    )
}