package com.bity.icp_kotlin_kit.candid.model

internal class CandidDictionaryItemType(
    val hashedKey: ULong,
    val type: CandidType
) {
    constructor(item: CandidDictionaryItem): this(
        hashedKey = item.hashedKey,
        type = item.value.candidType
    )
}