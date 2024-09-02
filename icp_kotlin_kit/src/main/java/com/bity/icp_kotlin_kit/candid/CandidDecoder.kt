package com.bity.icp_kotlin_kit.candid

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

// TODO, not able to parse nested classes
internal object CandidDecoder {

    inline fun <reified T : Any>decodeNullable(candidValue: CandidValue?): T? {
        candidValue ?: return null
        return decode(candidValue)
    }

    inline fun <reified T : Any>decode(candidValue: CandidValue): T {
        val res: Any = when(candidValue) {
            is CandidValue.Blob -> candidValue.data
            is CandidValue.Bool -> candidValue.bool
            CandidValue.Empty -> TODO()
            is CandidValue.Float32 -> candidValue.float
            is CandidValue.Float64 -> candidValue.double
            is CandidValue.Function -> TODO()
            is CandidValue.Integer -> candidValue.bigInt
            is CandidValue.Integer16 -> candidValue.int16
            is CandidValue.Integer32 -> candidValue.int32
            is CandidValue.Integer64 -> candidValue.int64
            is CandidValue.Integer8 -> candidValue.int8
            is CandidValue.Natural -> candidValue.bigUInt
            is CandidValue.Natural16 -> candidValue.uInt16
            is CandidValue.Natural32 -> candidValue.uInt32
            is CandidValue.Natural64 -> candidValue.uInt64
            is CandidValue.Natural8 -> candidValue.uInt8
            CandidValue.Null -> TODO()
            is CandidValue.Option -> TODO()
            is CandidValue.Record -> buildObject(candidValue.dictionary, T::class)
            CandidValue.Reserved -> TODO()
            is CandidValue.Text -> candidValue.string
            is CandidValue.Variant -> TODO()
            is CandidValue.Vector -> TODO()
        }
        return res as T
    }

    private fun <T : Any> buildObject(
        candidDictionary: CandidDictionary,
        clazz: KClass<T>
    ): T {
        val constructor = clazz.primaryConstructor ?: throw IllegalArgumentException("Class must have a primary constructor")
        val params = constructor.parameters.associateWith { param ->
            val candidValue = candidDictionary[param.name] ?: throw IllegalArgumentException("Missing value for parameter: ${param.name}")
            decodeNullable<Any>(candidValue)
        }
        return constructor.callBy(params)
    }
}