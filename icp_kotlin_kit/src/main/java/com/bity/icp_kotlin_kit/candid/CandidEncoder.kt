package com.bity.icp_kotlin_kit.candid

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidPrimitiveType
import com.bity.icp_kotlin_kit.candid.model.CandidType
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import java.math.BigInteger
import kotlin.reflect.full.memberProperties

internal object CandidEncoder {

    @OptIn(ExperimentalStdlibApi::class)
    operator fun invoke(arg: Any?, expectedClass: Class<*>? = null): CandidValue {
        return when(arg) {

            null -> {
                requireNotNull(expectedClass)
                CandidValue.Option(candidPrimitiveTypeForClass(expectedClass))
            }

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
                // TODO, value could be optional
                CandidValue.Record(
                    CandidDictionary(
                        arg::class.memberProperties.associate {
                            it.name to CandidEncoder(it.getter.call(arg))
                        }
                    )
                )
            }
        }
    }

    private fun candidPrimitiveTypeForClass(clazz: Class<*>): CandidType {
        return when(clazz) {
            Boolean::class.java -> CandidType.Primitive(CandidPrimitiveType.BOOL)
            String::class.java -> CandidType.Primitive(CandidPrimitiveType.TEXT)
            else -> TODO()
        }
    }
}