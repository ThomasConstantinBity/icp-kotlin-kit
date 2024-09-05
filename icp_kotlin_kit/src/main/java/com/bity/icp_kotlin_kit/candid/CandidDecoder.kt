package com.bity.icp_kotlin_kit.candid

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.valueParameters

// TODO, not able to parse nested classes
internal object CandidDecoder {

    inline fun <reified T>decodeNotNull(candidValue: CandidValue): T =
        decode<T>(candidValue) ?: throw RuntimeException("Value cannot be null")

    inline fun <reified T>decode(candidValue: CandidValue?): T? {
        candidValue ?: return null
        println("Decoding $candidValue into ${T::class}")
        val res = when(candidValue) {
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
            is CandidValue.Option -> decodeOption(candidValue.option.value)
            is CandidValue.Record -> TODO() // buildObject(candidValue.dictionary, T::class.constructors.first())

            CandidValue.Reserved -> TODO()
            is CandidValue.Text -> candidValue.string
            is CandidValue.Variant -> TODO()
            is CandidValue.Vector -> TODO()
        }
        return res as T
    }

    // TODO, can't return Any
    private fun decodeOption(candidValue: CandidValue?): Any? {
        candidValue ?: return null
        return decode(candidValue)
    }

    /*private fun <T> buildObject(
        candidDictionary: CandidDictionary,
        constructor: KFunction<T>,
    ): T {
        val params = constructor.valueParameters.associateWith { param ->
            val candidValue = candidDictionary[param.name] ?: throw IllegalArgumentException("Missing value for parameter: ${param.name}")
            val kClass = param.type.classifier as? KClass<*> ?: throw IllegalArgumentException("Unsupported type: ${param.type}")
            val res = decode<Any>(candidValue, kClass)
            res
        }
        val res = constructor.callBy(params)
        return res
    }

    inline fun <reified T: Any> decode(candidValue: CandidValue, kClass: KClass<*>): Any? {

        println("Decoding ${kClass} from $candidValue")

        return when {

            kClass == ULong::class -> decode<ULong>(candidValue)
            kClass == ByteArray::class -> decode<ByteArray>(candidValue)
            kClass == Float::class -> decode<Float>(candidValue)

            candidValue is CandidValue.Vector -> {
                val candidVector = candidValue.vector
                return if (candidVector.values.isEmpty()) emptyList<T>()
                else return candidVector.values.map { decode<T>(it) }
            }

            candidValue is CandidValue.Function -> {
                val inputArgs = candidValue.function.signature.inputs
                val outputArgs = candidValue.function.signature.outputs.size
            }

            else -> {
                (candidValue as? CandidValue.Record)?.let {
                    buildObject(
                        candidDictionary = it.dictionary,
                        constructor = kClass.constructors.first()
                    )
                } ?: throw Exception("Unsupported type: $kClass")
            }
        }
    }*/
}