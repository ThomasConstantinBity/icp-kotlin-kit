package com.bity.icp_kotlin_kit.data.model

// Candid Variant
sealed class CandidVariantError: Error() {
    class ValueNotPartOfTypes: CandidVariantError()
}

// Candid Vector
sealed class CandidVectorError: Error() {
    class NoElementsAndNoType: CandidVectorError()
    class WrongCandidType: CandidVectorError()
}