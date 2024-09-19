package com.bity.icp_kotlin_kit.candid.model

import com.bity.icp_kotlin_kit.data.model.CandidVectorError

internal data class CandidVector(
    val values: List<CandidValue>,
    val containedType: CandidType
) {
    constructor(containedType: CandidType): this(
        values = emptyList(),
        containedType = containedType
    )

    @Throws(CandidVectorError.NoElementsAndNoType::class)
    constructor(sequence: List<CandidValue>): this(
        values = sequence,
        containedType = sequence.firstOrNull()?.candidType
            ?: throw CandidVectorError.NoElementsAndNoType()
    )

    @Throws(CandidVectorError.WrongCandidType::class)
    constructor(type: CandidType, sequence: List<CandidValue>): this(
        values = sequence,
        containedType = type
    ) {
        if(sequence.isNotEmpty()) {
            sequence.find { it.candidType != type }?.let {
                throw CandidVectorError.WrongCandidType()
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CandidVector) return false
        return values == other.values && containedType == other.containedType
    }

    override fun hashCode(): Int {
        var result = values.hashCode()
        result = 31 * result + containedType.hashCode()
        return result
    }
}