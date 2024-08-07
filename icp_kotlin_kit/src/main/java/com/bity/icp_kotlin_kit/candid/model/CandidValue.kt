package com.bity.icp_kotlin_kit.candid.model

import java.math.BigInteger

internal sealed class CandidValue(
    val candidType: CandidType
) {
    val natural8Value: BigInteger?
        get() = (this as? Natural8)
            ?.let { BigInteger.valueOf(it.uInt8.toLong()) }

    val natural64Value: ULong?
        get() = (this as? Natural64)?.uInt64

    val recordValue: CandidDictionary?
        get() = (this as? Record)?.dictionary

    val blobValue: ByteArray?
        get() = (this as? Blob)?.data

    val optionValue: CandidOption?
        get() = (this as? Option)?.option

    val vectorValue: CandidVector?
        get() = (this as? Vector)?.vector

    val variantValue: CandidVariant?
        get() = (this as? Variant)?.variant

    val functionValue: CandidFunction?
        get() = (this as? Function)?.function

    data object Null : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.NULL
        )
    )
    class Bool(val bool: Boolean) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.BOOL
        )
    )
    class Natural(val bigUInt: BigInteger) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.NATURAL
        )
    )
    class Integer(val bigInt: BigInteger) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.INTEGER
        )
    )
    class Natural8(val uInt8: UByte) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.NATURAL8
        )
    )
    class Natural16(val uInt16: UShort) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.NATURAL16
        )
    )
    class Natural32(val uInt32: UInt) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.NATURAL32
        )
    )
    class Natural64(val uInt64: ULong) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.NATURAL64
        )
    )
    class Integer8(val int8: Byte) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.INTEGER8
        )
    )
    class Integer16(val int16: Short) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.INTEGER16
        )
    )
    class Integer32(val int32: Int) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.INTEGER32
        )
    )
    class Integer64(val int64: Long) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.INTEGER64
        )
    )
    class Float32(val float: Float) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.FLOAT32
        )
    )
    class Float64(val double: Double) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.FLOAT64
        )
    )
    class Text(val string: String) : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.TEXT
        )
    )
    class Blob(val data: ByteArray) : CandidValue(
        candidType = CandidType.Container(
            primitiveType = CandidPrimitiveType.VECTOR,
            type = CandidType.Primitive(
                primitiveType = CandidPrimitiveType.NATURAL8
            )
        )
    )
    data object Reserved : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.RESERVED
        )
    )
    data object Empty : CandidValue(
        candidType = CandidType.Primitive(
            primitiveType = CandidPrimitiveType.EMPTY
        )
    )

    class Option(val option: CandidOption): CandidValue(
        candidType = CandidType.Container(
            primitiveType = CandidPrimitiveType.OPTION,
            type = option.containedType
        )
    ) {
        constructor(value: CandidValue): this(CandidOption.Some(value))

        constructor(containedType: CandidType): this(CandidOption.None(containedType))

        constructor(containedType: CandidPrimitiveType): this(CandidType.Primitive(containedType))

        /* fun Option.Companion.init(
            containedType: CandidType,
            value: CandidValue?
        ): Option {
            value?.let {
                return CandidValue.Option.init(it)
            }
            return CandidValue.Option.init(containedType)
        } */
    }
    class Vector(val vector: CandidVector) : CandidValue(
        candidType = CandidType.Container(
            primitiveType = CandidPrimitiveType.VECTOR,
            type = vector.containedType
        )
    ) {
        constructor(containedType: CandidType): this(CandidVector(containedType))
        constructor(containedType: CandidPrimitiveType): this(CandidVector(containedType))
    }
    class Record(val dictionary: CandidDictionary) : CandidValue(
        candidType = CandidType.KeyedContainer(
            primitiveType = CandidPrimitiveType.RECORD,
            dictionaryItemType = dictionary.candidTypes
        )
    )
    class Variant(val variant: CandidVariant) : CandidValue(
        candidType = CandidType.KeyedContainer(
            CandidPrimitiveType.VARIANT,
            variant.candidTypes
        )
    )
    class Function(val function: CandidFunction) : CandidValue(
        candidType = CandidType.Function(
            signature = function.signature
        )
    )
    // class Service(val service: CandidService) : CandidValue()

    override fun equals(other: Any?): Boolean {
        other ?: return false
        return when {
            this is Null && other is Null -> true
            this is Blob && other is Blob -> data.contentEquals(other.data)
            this is Bool && other is Bool -> bool == other.bool
            this is Empty && other is Empty -> true
            this is Float32 && other is Float32 -> float == other.float
            this is Float64 && other is Float64 -> double == other.double
            this is Function && other is Function -> function == other.function
            this is Integer && other is Integer -> bigInt == other.bigInt
            this is Integer16 && other is Integer16 -> int16 == other.int16
            this is Integer32 && other is Integer32 -> int32 == other.int32
            this is Integer64 && other is Integer64 -> int64 == other.int64
            this is Integer8 && other is Integer8 -> int8 == other.int8
            this is Natural && other is Natural -> bigUInt == other.bigUInt
            this is Natural16 && other is Natural16 -> uInt16 == other.uInt16
            this is Natural32 && other is Natural32 -> uInt32 == other.uInt32
            this is Natural64 && other is Natural64 -> uInt64 == other.uInt64
            this is Natural8 && other is Natural8 -> uInt8 == other.uInt8
            this is Option && other is Option -> option == other.option
            this is Record && other is Record -> dictionary == other.dictionary
            this is Reserved && other is Reserved -> true
            this is Text && other is Text -> string == other.string
            this is Variant && other is Variant -> variant == other.variant
            this is Vector && other is Vector -> vector == other.vector
            else -> false
        }
    }

    override fun hashCode(): Int {
        return candidType.hashCode()
    }
}