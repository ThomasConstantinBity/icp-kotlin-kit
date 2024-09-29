package com.bity.icp_kotlin_kit.candid

import com.bity.icp_kotlin_kit.candid.model.CandidOption
import com.bity.icp_kotlin_kit.candid.model.CandidPrincipal
import com.bity.icp_kotlin_kit.candid.model.CandidRecord
import com.bity.icp_kotlin_kit.candid.model.CandidType
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

internal object CandidEncoder {

    // TODO, expectedClass could be removed
    operator fun invoke(
        arg: Any?,
        expectedClass: KClass<*>? = null,
        expectedClassNullable: Boolean = false
    ): CandidValue {

        if(arg == null) {
            requireNotNull(expectedClass)
            return CandidValue.Option(
                option = CandidOption.None(
                    type = candidPrimitiveTypeForClass(expectedClass)
                )
            )
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

            is ICPPrincipal -> CandidValue.Principal(
                candidPrincipal = CandidPrincipal(
                    string = arg.string,
                    bytes = arg.bytes
                )
            )

            else -> {
                // TODO, value could be optional
                val dictionary = arg::class.memberProperties.associate {
                    // Required if obfuscation is enabled
                    it.isAccessible = true
                    it.name to CandidEncoder(
                        arg = it.getter.call(arg),
                        expectedClass = it.returnType.jvmErasure,
                        expectedClassNullable = it.returnType.isMarkedNullable
                    )
                }.toMap()
                CandidValue.Record(CandidRecord.init(dictionary))
            }
        }
        return if(expectedClassNullable) {
            requireNotNull(expectedClass)
            CandidValue.Option(candidPrimitiveTypeForClass(expectedClass))
        }
        else candidValue
    }

    // TODO return CandidValue.Option
    private fun candidPrimitiveTypeForClass(clazz: KClass<*>): CandidType {
        return when(clazz) {

            Byte::class-> CandidType.Integer8

            ByteArray::class -> CandidType.Vector(CandidType.Integer8)

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

            else -> TODO("not implemented for $clazz")
        }
    }
}