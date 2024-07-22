package com.bity.icp_candid.domain.model

sealed class CandidType(
    val primitiveType: CandidPrimitiveType
) {
    val containedType: CandidType?
        get() = (this as? Container)?.type

    val keyedContainerRowTypes: List<CandidDictionaryItemType>?
        get() = (this as? KeyedContainer)?.dictionaryItemType

    val functionSignature: CandidFunction.CandidFunctionSignature?
        get() = (this as? Function)?.signature

    class Primitive(
        primitiveType: CandidPrimitiveType
    ) : CandidType(primitiveType = primitiveType)

    class Container(
        primitiveType: CandidPrimitiveType,
        val type: CandidType
    ) : CandidType(primitiveType = primitiveType)

    class KeyedContainer(
        primitiveType: CandidPrimitiveType,
        val dictionaryItemType: List<CandidDictionaryItemType>
    ) : CandidType(primitiveType = primitiveType)

    class Function(
        val signature: CandidFunction.CandidFunctionSignature
    ) : CandidType(primitiveType = CandidPrimitiveType.FUNCTION)

    /* class Service(
        val methods: List<CandidService.Method>
    ): CandidType() */

    override fun equals(other: Any?): Boolean {
        return when {
            this is Primitive && other is Primitive ->
                primitiveType.value == other.primitiveType.value

            this is Container && other is Container ->
                primitiveType.value == other.primitiveType.value
                        && type == other.type

            this is KeyedContainer && other is KeyedContainer ->
                primitiveType.value == other.primitiveType.value
                        && dictionaryItemType == other.dictionaryItemType

            this is Function && other is Function ->
                functionSignature == other.functionSignature

            else -> false
        }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}