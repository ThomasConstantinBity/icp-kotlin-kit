package com.bity.icp_kotlin_kit.candid

import com.bity.icp_kotlin_kit.candid.model.CandidType
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import java.math.BigInteger

internal object CandidEncoder {
    operator fun invoke(
        arg: Any?,
        expectedClass: Class<*>? = null,
        expectedClassNullable: Boolean = false
    ): CandidValue {

        if(arg == null) {
            requireNotNull(expectedClass)
            return CandidValue.Option(candidPrimitiveTypeForClass(expectedClass))
        }

        val candidValue = when(arg) {

            // Unsigned value
            is UByte -> CandidValue.Natural8(arg)
            is UShort -> CandidValue.Natural16(arg)
            is UInt -> CandidValue.Natural32(arg)
            is ULong -> CandidValue.Natural64(arg)

            // Signed value
            is Byte -> CandidValue.Integer8(arg)
            is Short -> CandidValue.Integer16(arg)
            is Int -> CandidValue.Integer32(arg)
            is Long -> CandidValue.Integer64(arg)

            is Float -> CandidValue.Float32(arg)
            is Double -> CandidValue.Float64(arg)

            // TODO
            is BigInteger -> TODO()

            is Boolean -> CandidValue.Bool(arg)
            is String -> CandidValue.Text(arg)
            is ByteArray -> CandidValue.Blob(arg)

            else -> {
                TODO()
                // TODO, value could be optional
                /* CandidValue.Record(
                    CandidDictionary(
                        arg::class.memberProperties.associate {
                            it.name to CandidEncoder(it.getter.call(arg))
                        }
                    )
                ) */
            }
        }
        return if(expectedClassNullable) {
            requireNotNull(expectedClass)
            CandidValue.Option(candidPrimitiveTypeForClass(expectedClass))
        }
        else candidValue
    }

    // TODO return CandidValue.Option
    private fun candidPrimitiveTypeForClass(clazz: Class<*>): CandidType {
        return when(clazz) {

            /**
            // Unsigned Value
            UByte::class.java-> CandidType.Primitive(CandidPrimitiveType.NATURAL8)
            UShort::class.java -> CandidType.Primitive(CandidPrimitiveType.NATURAL16)
            UInt::class.java -> CandidType.Primitive(CandidPrimitiveType.NATURAL32)
            ULong::class.java -> CandidType.Primitive(CandidPrimitiveType.NATURAL64)

            // Signed Value
            Byte::class.java-> CandidType.Primitive(CandidPrimitiveType.INTEGER8)
            Short::class.java -> CandidType.Primitive(CandidPrimitiveType.INTEGER16)
            Int::class.java -> CandidType.Primitive(CandidPrimitiveType.INTEGER32)
            Long::class.java -> CandidType.Primitive(CandidPrimitiveType.INTEGER64)

            Float::class.java -> CandidType.Primitive(CandidPrimitiveType.FLOAT32)
            Double::class.java -> CandidType.Primitive(CandidPrimitiveType.FLOAT64)

            Boolean::class.java -> CandidType.Primitive(CandidPrimitiveType.BOOL)
            String::class.java -> CandidType.Primitive(CandidPrimitiveType.TEXT)
            **/

            else -> TODO()
        }
    }
}