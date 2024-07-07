package com.bity.icp_candid.domain.model

class CandidDictionaryItem(
    val hashedKey: ULong,
    val value: CandidValue
) {
    constructor(key: String, value: CandidValue): this(
        hashedKey = CandidDictionary.hash(key),
        value = value
    )
}