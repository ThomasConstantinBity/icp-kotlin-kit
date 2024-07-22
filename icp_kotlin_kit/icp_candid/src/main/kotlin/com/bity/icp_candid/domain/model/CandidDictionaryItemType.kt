package com.bity.icp_candid.domain.model

class CandidDictionaryItemType(
    val hashedKey: ULong,
    val type: CandidType
) {
    constructor(item: CandidDictionaryItem): this(
        hashedKey = item.hashedKey,
        type = item.value.candidType
    )

    constructor(key: String, type: CandidType): this(
        hashedKey = CandidDictionary.hash(key),
        type = type
    )

    override fun equals(other: Any?): Boolean {
        if(other !is CandidDictionaryItemType) return false
        return hashedKey == other.hashedKey && type == other.type
    }

    override fun hashCode(): Int {
        var result = hashedKey.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}