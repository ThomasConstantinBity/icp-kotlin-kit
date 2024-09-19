package com.bity.icp_kotlin_kit.candid.model

internal class CandidKeyedType(
    val key: CandidKey,
    val type: CandidType
) {

    constructor(key: Long, type: CandidType): this(
        key = CandidKey(key),
        type = type
    )

    constructor(value: CandidKeyedValue): this(
        key = value.key,
        type = value.value.candidType
    )
}