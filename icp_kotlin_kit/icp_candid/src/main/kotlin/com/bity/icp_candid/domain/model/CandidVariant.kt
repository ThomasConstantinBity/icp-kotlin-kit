package com.bity.icp_candid.domain.model

import com.bity.icp_candid.domain.model.error.CandidVariantError

class CandidVariant(
    val candidTypes: List<CandidDictionaryItemType>,
    val value: CandidValue,
    val valueIndex: ULong
) {

    val hashedKey: ULong = candidTypes[valueIndex.toInt()].hashedKey

    constructor(
        candidTypes: HashMap<String, CandidType>,
        value: Pair<String, CandidValue>
    ) : this(
        candidTypes = candidTypes.map { CandidDictionaryItemType(it.key, it.value) },
        value = value.second,
        valueIndex = candidTypes
            .keys
            .indexOfFirst { it == value.first }
            .toULong()
            .also {
                if (it < 0U) {
                    throw CandidVariantError.ValueNotPartOfTypes()
                }
            }
    )

    operator fun get(key: String): CandidValue? =
        this[CandidDictionary.hash(key)]

    operator fun get(key: ULong): CandidValue? {
        require(hashedKey == key) {
            return null
        }
        return value
    }

    override fun equals(other: Any?): Boolean {
        if(other !is CandidVariant) return false
        return candidTypes == other.candidTypes
                &&  value == other.value
                && valueIndex == other.valueIndex
    }

    override fun hashCode(): Int {
        var result = candidTypes.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + valueIndex.hashCode()
        result = 31 * result + hashedKey.hashCode()
        return result
    }
}