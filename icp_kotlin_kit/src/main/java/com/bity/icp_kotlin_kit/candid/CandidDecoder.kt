package com.bity.icp_kotlin_kit.candid

import com.bity.icp_kotlin_kit.candid.model.CandidRecord
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.candid.model.CandidVector
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import java.lang.RuntimeException
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters

internal object CandidDecoder {

    inline fun <reified T>decodeNotNull(candidValue: CandidValue): T =
        decode<T>(candidValue) ?: throw RuntimeException("Value cannot be null")

    inline fun <reified T>decode(candidValue: CandidValue?): T? {
        candidValue ?: return null
        val clazz = T::class
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
            is CandidValue.Option -> TODO() // decodeOption(candidValue.option.value)
            is CandidValue.Record -> {
                buildObject(
                    candidRecord = candidValue.record,
                    constructor = T::class.constructors.first()
                )
            }

            CandidValue.Reserved -> TODO()
            is CandidValue.Text -> candidValue.string
            is CandidValue.Variant -> {
                buildSealedClass(
                    candidValue = candidValue.variant.value,
                    subclasses = T::class.nestedClasses.toList()
                )
            }

            is CandidValue.Vector -> {
                require(clazz.java.isArray)
                val componentType = clazz.java.componentType.kotlin
                buildArray(
                    candidVector = candidValue.vector,
                    componentType = componentType
                )
            }

            is CandidValue.Principal -> TODO()
            is CandidValue.Service -> TODO()
        }
        return res as T
    }

    private fun <T> buildObject(
        candidRecord: CandidRecord,
        constructor: KFunction<T>,
    ): T {
        val params = constructor.valueParameters.mapIndexed { index, param ->
            val res = getValueForParam(param, candidRecord, index)
            param to res
        }.toMap()
        return constructor.callBy(params)
    }

    private fun getValueForParam(
        param: KParameter,
        candidRecord: CandidRecord,
        index: Int,
    ): Any? {
        val key = param.name
        requireNotNull(key)
        val value = candidRecord[key]
            ?: candidRecord[index.toULong()]
            ?: throw IllegalArgumentException("Missing value for parameter: ${param.name}")
        return decode(value, param.type)
    }

    private fun decode(candidValue: CandidValue, type: KType): Any? {
         return when(candidValue) {
            is CandidValue.Blob -> candidValue.data
            is CandidValue.Bool -> candidValue.bool
            CandidValue.Empty -> TODO()
            is CandidValue.Float32 -> candidValue.float
            is CandidValue.Float64 -> candidValue.double
            is CandidValue.Function -> {
                // TODO, support additional constructor
                val name = candidValue.function.method?.name
                val principalId = candidValue.function.method?.principal?.bytes?.let { ICPPrincipal(it) }
                return (type.classifier as KClass<*>).primaryConstructor!!.call(name!!, principalId)
            }
            is CandidValue.Integer -> candidValue.bigInt
            is CandidValue.Integer16 -> candidValue.int16
            is CandidValue.Integer32 -> candidValue.int32
            is CandidValue.Integer64 -> candidValue.int64
            is CandidValue.Integer8 -> candidValue.int8
            is CandidValue.Natural -> candidValue.bigUInt
            is CandidValue.Natural16 -> candidValue.uInt16
            is CandidValue.Natural32 -> candidValue.uInt32
            is CandidValue.Natural64 -> candidValue.uInt64
            is CandidValue.Natural8 -> TODO()
            CandidValue.Null -> TODO()
            is CandidValue.Option -> candidValue.option.value?.let { decode(it, type) }

            is CandidValue.Principal ->
                candidValue.candidPrincipal?.bytes?.let {
                    ICPPrincipal(it)
                }
            is CandidValue.Record -> {
                buildObject(
                    candidRecord = candidValue.record,
                    constructor = (type.classifier as KClass<*>).primaryConstructor!!
                )
            }
            CandidValue.Reserved -> TODO()
            is CandidValue.Service -> TODO()
            is CandidValue.Text -> candidValue.string
            is CandidValue.Variant -> {
                // TODO, should have a name
                val kClass = type.classifier as? KClass<*>
                requireNotNull(kClass)
                require(kClass.isSealed)
                buildSealedClass(
                    candidValue = candidValue.variant.value,
                    subclasses = kClass.sealedSubclasses
                )
            }
            is CandidValue.Vector -> {
                val componentType = type.arguments.firstOrNull()?.type?.classifier as? KClass<*>
                requireNotNull(componentType)
                buildArray(
                    candidVector = candidValue.vector,
                    componentType = componentType
                )
            }
        }
    }

    private fun buildSealedClass(
        candidValue: CandidValue,
        subclasses: List<KClass<out Any>>
    ): Any {
        return when(candidValue) {
            is CandidValue.Blob -> buildDataClass(subclasses, ByteArray::class, candidValue.data)
            is CandidValue.Bool -> buildDataClass(subclasses, Boolean::class, candidValue.bool)
            CandidValue.Empty -> TODO()
            is CandidValue.Float32 -> buildDataClass(subclasses, Float::class, candidValue.float)
            is CandidValue.Float64 -> buildDataClass(subclasses, Double::class, candidValue.double)
            is CandidValue.Function -> TODO()
            is CandidValue.Integer -> buildDataClass(subclasses, BigInteger::class, candidValue.bigInt)
            is CandidValue.Integer16 -> buildDataClass(subclasses, Short::class, candidValue.int16)
            is CandidValue.Integer32 -> buildDataClass(subclasses, Int::class, candidValue.int32)
            is CandidValue.Integer64 -> buildDataClass(subclasses, Long::class, candidValue.int64)
            is CandidValue.Integer8 -> buildDataClass(subclasses, Byte::class, candidValue.int8)
            is CandidValue.Natural -> buildDataClass(subclasses, BigInteger::class, candidValue.bigUInt)
            is CandidValue.Natural16 -> buildDataClass(subclasses, UShort::class, candidValue.uInt16)
            is CandidValue.Natural32 -> buildDataClass(subclasses, UInt::class, candidValue.uInt32)
            is CandidValue.Natural64 -> buildDataClass(subclasses, ULong::class, candidValue.uInt64)
            is CandidValue.Natural8 -> buildDataClass(subclasses, UByte::class, candidValue.uInt8)
            CandidValue.Null -> {
                // TODO, check value
                buildObject(subclasses)
            }
            is CandidValue.Option -> TODO()
            is CandidValue.Principal -> TODO()
            is CandidValue.Record -> buildDataClass(candidValue.record, subclasses)
            CandidValue.Reserved -> TODO()
            is CandidValue.Service -> TODO()

            is CandidValue.Text -> buildDataClass(subclasses, String::class, candidValue.string)
            is CandidValue.Variant -> TODO()

            is CandidValue.Vector -> TODO()
        }
    }

    private fun buildObject(
        subclasses: List<KClass<out Any>>,
    ): Any {
        val targetClasses = subclasses
            .filter { it.primaryConstructor == null }
        require(targetClasses.isNotEmpty())
        return if(targetClasses.size == 1) {
            targetClasses.first().objectInstance!!
        } else
            // TODO()
            targetClasses.first().objectInstance!!
    }

    // TODO
    //  if .did file will be updated some fields will be added/removed, candid records will
    //  have a different number of param and an error will be thrown
    private fun buildDataClass(
        candidRecord: CandidRecord,
        subclasses: List<KClass<out Any>>
    ): Any {
        val paramsNumber = candidRecord.candidSortedItems.size
        require(paramsNumber > 0)
        val targetClasses = subclasses.filter { it.primaryConstructor?.parameters?.size == paramsNumber }
        return if(targetClasses.size == 1) {
            val constructor = targetClasses.first().primaryConstructor
            requireNotNull(constructor)
            val params = constructor.parameters.associateWith { param ->
                val res = decode(candidRecord, param)
                res
            }
            constructor.callBy(params)
        } else TODO()
    }

    private fun getPrimitiveValueForKey(candidRecord: CandidRecord, key: String): Any? {
        val value = candidRecord[key]
        requireNotNull(value)
        return getPrimitiveValue(value)
    }

    private fun getPrimitiveValue(candidValue: CandidValue): Any? =
        when(candidValue) {
            is CandidValue.Blob -> candidValue.data
            is CandidValue.Bool -> candidValue.bool
            CandidValue.Empty -> TODO()
            is CandidValue.Float32 -> candidValue.float
            is CandidValue.Float64 -> candidValue.double
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
            CandidValue.Null -> null
            is CandidValue.Option -> {
                candidValue.option.value?.let {
                    getPrimitiveValueForKey(
                        CandidRecord.init(mapOf("key" to it)),
                        "key"
                    )
                }
            }
            is CandidValue.Principal -> TODO()
            CandidValue.Reserved -> TODO()
            is CandidValue.Text -> candidValue.string

            else -> throw RuntimeException("Can't get primitive type from ${candidValue::class.java.simpleName}")
        }

    private fun decode(candidRecord: CandidRecord, param: KParameter): Any? {

        val key = param.name
        requireNotNull(key)

        return when(val classifier = param.type.classifier as KClass<*>) {

            ByteArray::class,
            Byte::class,
            Short::class,
            Int::class,
            Long::class,
            UByte::class,
            UShort::class,
            UInt::class,
            ULong::class,
            Float::class,
            Double::class,
            String::class,
            Boolean::class,
            BigInteger::class,
            Char::class -> getPrimitiveValueForKey(candidRecord, key)

            else -> {
                val candidValue = candidRecord[key] as? CandidValue.Record

                /**
                 * Candid value is null if class has been generated and generic name has been assigned to value.
                 * Ex: LedgerCanister.QueryArchiveResult.Ok::blockRange
                 */
                if(candidValue == null) {
                    createClass(candidRecord, classifier)
                } else {
                    decode(
                        candidValue = candidValue,
                        type = param.type
                    )
                }
            }
        }
    }

    private fun createClass(
        candidRecord: CandidRecord,
        classifier: KClass<*>
    ): Any {
        val constructor = classifier.primaryConstructor
        requireNotNull(constructor)
        val params = constructor.parameters.associateWith {
            val key = it.name
            requireNotNull(key)
            val candidValue = candidRecord[key]
            requireNotNull(candidValue)
            decode(
                candidValue = candidValue,
                type = it.type
            )
        }
        return constructor.callBy(params)
    }

    private fun buildDataClass(
        subclasses: List<KClass<out Any>>,
        kClass: KClass<*>,
        value: Any
    ): Any {
        val targetClasses = subclasses
            .filter { it.primaryConstructor?.parameters?.size == 1 }
            .filter { it.primaryConstructor!!.parameters.first().type.classifier == kClass  }
        require(targetClasses.isNotEmpty())
        return if(targetClasses.size == 1) {
            targetClasses.first().primaryConstructor!!.call(value)
        } else TODO()
    }

    private fun buildArray (
        candidVector: CandidVector,
        componentType: KClass<*>
    ): Any? {
        return if (candidVector.values.isEmpty())
            java.lang.reflect.Array.newInstance(componentType.java, 0)
        else {
            val newArray = java.lang.reflect.Array.newInstance(
                componentType.java,
                candidVector.values.size
            ) as Array<Any?>
            val arrayValues = candidVector.values.map { value ->
                when(value) {
                    is CandidValue.Blob -> value.data
                    is CandidValue.Bool -> value.bool
                    CandidValue.Empty -> TODO()
                    is CandidValue.Float32 -> value.float
                    is CandidValue.Float64 -> value.double
                    is CandidValue.Function -> TODO()
                    is CandidValue.Integer -> value.bigInt
                    is CandidValue.Integer16 -> value.int16
                    is CandidValue.Integer32 -> value.int32
                    is CandidValue.Integer64 -> value.int64
                    is CandidValue.Integer8 -> value.int8
                    is CandidValue.Natural -> value.bigUInt
                    is CandidValue.Natural16 -> value.uInt16
                    is CandidValue.Natural32 -> value.uInt32
                    is CandidValue.Natural64 -> value.uInt64
                    is CandidValue.Natural8 -> value
                    CandidValue.Null -> null
                    is CandidValue.Option -> TODO()
                    is CandidValue.Record -> {
                        buildObject(
                            candidRecord = value.record,
                            constructor = componentType.constructors.first()
                        )
                    }
                    CandidValue.Reserved -> TODO()
                    is CandidValue.Text -> value.string
                    is CandidValue.Variant -> TODO()
                    is CandidValue.Vector -> TODO()
                    is CandidValue.Principal -> TODO()
                    is CandidValue.Service -> TODO()
                }
            }.toTypedArray()
            arrayValues.copyInto(newArray, 0, 0, candidVector.values.size)
        }
    }
}