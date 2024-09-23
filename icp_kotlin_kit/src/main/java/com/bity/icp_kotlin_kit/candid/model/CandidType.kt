package com.bity.icp_kotlin_kit.candid.model

internal sealed class CandidType(
    private val candidPrimitiveType: CandidPrimitiveType?
) {
    val functionSignature: CandidFunctionSignature?
        get() = (this as? Function)?.signature

    val serviceSignature: CandidServiceSignature?
        get() = (this as? Service)?.serviceSignature

    val primitiveType: CandidPrimitiveType
        get() = candidPrimitiveType
            ?: throw Error("Primitive type for CandidTypeNamed should never be called")

    data object Null: CandidType(CandidPrimitiveType.NULL)
    data object Bool: CandidType(CandidPrimitiveType.BOOL)
    data object Natural: CandidType(CandidPrimitiveType.NATURAL)
    data object Integer: CandidType(CandidPrimitiveType.INTEGER)
    data object Natural8: CandidType(CandidPrimitiveType.NATURAL8)
    data object Natural16: CandidType(CandidPrimitiveType.NATURAL16)
    data object Natural32: CandidType(CandidPrimitiveType.NATURAL32)
    data object Natural64: CandidType(CandidPrimitiveType.NATURAL64)
    data object Integer8: CandidType(CandidPrimitiveType.INTEGER8)
    data object Integer16: CandidType(CandidPrimitiveType.INTEGER16)
    data object Integer32: CandidType(CandidPrimitiveType.INTEGER32)
    data object Integer64: CandidType(CandidPrimitiveType.INTEGER64)
    data object Float32: CandidType(CandidPrimitiveType.FLOAT32)
    data object Float64: CandidType(CandidPrimitiveType.FLOAT64)
    data object Text: CandidType(CandidPrimitiveType.TEXT)
    data object Reserved: CandidType(CandidPrimitiveType.RESERVED)
    data object Empty: CandidType(CandidPrimitiveType.EMPTY)

    data class Option(
        val candidType: CandidType
    ): CandidType(CandidPrimitiveType.OPTION)

    data class Vector(
        val candidType: CandidType
    ): CandidType(CandidPrimitiveType.VECTOR)

    data class Record(
        val candidKeyedTypes: CandidKeyedTypes
    ): CandidType(CandidPrimitiveType.RECORD) {
        constructor(containedTypes: List<CandidKeyedType>): this(
            candidKeyedTypes = CandidKeyedTypes(containedTypes)
        )
    }

    data class Variant(
        val candidKeyedTypes: CandidKeyedTypes
    ): CandidType(CandidPrimitiveType.VARIANT) {
        constructor(containedTypes: List<CandidKeyedType>): this(
            candidKeyedTypes = CandidKeyedTypes(containedTypes)
        )
    }

    data class Function(
        val signature: CandidFunctionSignature
    ) : CandidType(CandidPrimitiveType.FUNCTION)

    data class Service(
        val candidServiceSignature: CandidServiceSignature
    ): CandidType(CandidPrimitiveType.SERVICE)

    data object Principal: CandidType(CandidPrimitiveType.PRINCIPAL)

    data class Named(
        val string: String
    ): CandidType(null)

    companion object {
        fun init(primitiveType: CandidPrimitiveType): CandidType? {
            return when(primitiveType) {
                CandidPrimitiveType.NULL -> Null
                CandidPrimitiveType.BOOL -> Bool
                CandidPrimitiveType.NATURAL -> Natural
                CandidPrimitiveType.INTEGER -> Integer
                CandidPrimitiveType.NATURAL8 -> Natural8
                CandidPrimitiveType.NATURAL16 -> Natural16
                CandidPrimitiveType.NATURAL32 -> Natural32
                CandidPrimitiveType.NATURAL64 -> Natural64
                CandidPrimitiveType.INTEGER8 -> Integer8
                CandidPrimitiveType.INTEGER16 -> Integer16
                CandidPrimitiveType.INTEGER32 -> Integer32
                CandidPrimitiveType.INTEGER64 -> Integer64
                CandidPrimitiveType.FLOAT32 -> Float32
                CandidPrimitiveType.FLOAT64 -> Float64
                CandidPrimitiveType.TEXT -> Text
                CandidPrimitiveType.RESERVED -> Reserved
                CandidPrimitiveType.EMPTY -> Empty

                // these are composite types, should not be deduced from primitives
                CandidPrimitiveType.OPTION,
                CandidPrimitiveType.VECTOR,
                CandidPrimitiveType.RECORD,
                CandidPrimitiveType.VARIANT,
                CandidPrimitiveType.FUNCTION,
                CandidPrimitiveType.SERVICE -> null

                CandidPrimitiveType.PRINCIPAL -> Principal
            }
        }
    }
}