package com.bity.icp_kotlin_kit.candid.model

internal class CandidVariant(
    candidTypesList: List<CandidKeyedType>,
    val value: CandidValue,
    val valueIndex: ULong
) {
    val candidTypes = candidTypesList.sortedBy { it.key }
    val key = candidTypes[valueIndex.toInt()].key
}