package com.bity.icp_kotlin_kit.candid

import com.bity.icp_kotlin_kit.candid.model.CandidRecord
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.candid.model.CandidVector
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import java.lang.RuntimeException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

// TODO
// - remove println
internal object CandidDecoder {

    inline fun <reified T>decodeNotNull(candidValue: CandidValue): T =
        decode<T>(candidValue) ?: throw RuntimeException("Value cannot be null")

    inline fun <reified T>decode(candidValue: CandidValue?): T? {
        candidValue ?: return null
        println("[Decode] - Decoding into ${T::class.java.simpleName}")
        // println("candidValue: $candidValue")
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
            is CandidValue.Option -> decodeOption(candidValue.option.value)
            is CandidValue.Record -> {
                TODO()
                /**
                 * buildObject(
                 *                 candidValue.dictionary,
                 *                 T::class.constructors.first()
                 *             )
                 */
            }

            CandidValue.Reserved -> TODO()
            is CandidValue.Text -> candidValue.string
            is CandidValue.Variant -> {
                require(T::class.isSealed) {
                    throw RuntimeException("Can't parse CandidVariant")
                }
                val nestedClasses = T::class.nestedClasses
                nestedClasses.firstNotNullOfOrNull {
                    try {
                        /*println(
                            """
                                [${it.simpleName}] - ${it.constructors.first().valueParameters.joinToString { p -> "${p.name}: ${p.type}" }}
                            """.trimIndent()
                        )*/
                        // TODO, support multiple args
                        val constructorArguments = decode(
                            candidValue = candidValue.variant.value,
                            type = it.constructors.first().parameters.first().type
                        )

                        // println("constructorArguments: $constructorArguments")
                        val res = it.constructors.first().call(constructorArguments)
                        // println("RES: $res")
                        res
                    } catch (t: Throwable) {
                        // t.printStackTrace()
                        // println("Error for ${it.simpleName}")
                        null
                    }
                }
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

    // TODO, can't return Any
    private fun decodeOption(candidValue: CandidValue?): Any? {
        candidValue ?: return null
        return decode(candidValue)
    }

    private fun <T> buildObject(
        candidRecord: CandidRecord,
        constructor: KFunction<T>,
    ): T {
        val params = constructor.valueParameters.mapIndexed { index, param ->
            val key = param.name
            requireNotNull(key)
            val value = candidRecord[key]
                ?: candidRecord[index.toULong()]
                ?: throw IllegalArgumentException("Missing value for parameter: ${param.name}")
            // println("[BuildObject] - ${param.name} - ${param.type.classifier}")
            val res = decode(value, param.type)
            println("[BuildObject] - ${param.name}: $res")
            param to res
        }.toMap()

        // println("[BuildObject] - calling constructor for $constructor")
        val res = constructor.callBy(params)
        // println("[BuildObject] - $res")
        return res
    }

    private fun decode(candidValue: CandidValue, type: KType): Any? {
        return when(candidValue) {
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
            is CandidValue.Natural8 -> TODO()
            CandidValue.Null -> TODO()
            is CandidValue.Option -> candidValue.option.value?.let { decode(it, type) }

            is CandidValue.Principal ->
                candidValue.candidPrincipal?.bytes?.let {
                    ICPPrincipal(it)
                }
            is CandidValue.Record -> TODO()
            CandidValue.Reserved -> TODO()
            is CandidValue.Service -> TODO()
            is CandidValue.Text -> candidValue.string
            is CandidValue.Variant -> {
                val kClass = type.classifier as? KClass<*>
                requireNotNull(kClass)
                require(kClass.isSealed)
                println(
                    """
                        candidValue: ${candidValue.variant.candidTypes.size}
                    """.trimIndent()
                )
                kClass.sealedSubclasses.map {
                    println(
                        """
                            $it has ${it.primaryConstructor?.parameters?.size ?: 0} params
                        """.trimIndent()
                    )
                }
                TODO()
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
            ) as Array<Any>
            val arrayValues = candidVector.values.map { value ->
                when(value) {
                    is CandidValue.Blob -> TODO()
                    is CandidValue.Bool -> TODO()
                    CandidValue.Empty -> TODO()
                    is CandidValue.Float32 -> TODO()
                    is CandidValue.Float64 -> TODO()
                    is CandidValue.Function -> TODO()
                    is CandidValue.Integer -> TODO()
                    is CandidValue.Integer16 -> TODO()
                    is CandidValue.Integer32 -> TODO()
                    is CandidValue.Integer64 -> TODO()
                    is CandidValue.Integer8 -> TODO()
                    is CandidValue.Natural -> TODO()
                    is CandidValue.Natural16 -> TODO()
                    is CandidValue.Natural32 -> TODO()
                    is CandidValue.Natural64 -> TODO()
                    is CandidValue.Natural8 -> TODO()
                    CandidValue.Null -> TODO()
                    is CandidValue.Option -> TODO()
                    is CandidValue.Record -> {
                        buildObject(
                            candidRecord = value.record,
                            constructor = componentType.constructors.first()
                        )
                    }
                    CandidValue.Reserved -> TODO()
                    is CandidValue.Text -> TODO()
                    is CandidValue.Variant -> TODO()
                    is CandidValue.Vector -> TODO()
                    is CandidValue.Principal -> TODO()
                    is CandidValue.Service -> TODO()
                }
            }.toTypedArray()
            arrayValues.copyInto(newArray, 0, 0, candidVector.values.size)
        }
    }

    fun _decode(candidValue: CandidValue, type: KType): Any? {

        val kClass = type.classifier
        // println("[ObjectBuilderDecoder] - componentType: ${type.classifier}")

        // TODO, use only candid value
        return when {

            kClass == ULong::class -> decode<ULong>(candidValue)
            kClass == ByteArray::class -> decode<ByteArray>(candidValue)
            kClass == Float::class -> decode<Float>(candidValue)

            candidValue is CandidValue.Vector -> {
                val componentType = type.arguments.firstOrNull()?.type?.classifier as? KClass<*>
                    // TODO, update message
                    ?: throw RuntimeException("no type found")
                // println("[ObjectBuilderDecoder] - componentType: $componentType")
                val candidVector = candidValue.vector
                if (candidVector.values.isEmpty())
                    return java.lang.reflect.Array.newInstance(componentType.java, 0)
                else {
                    TODO()
                    /*val newArray = java.lang.reflect.Array.newInstance(
                        componentType.java,
                        candidVector.values.size
                    ) as Array<Any>
                    val arrayValues = candidVector.values.map { value ->
                        when(value) {
                            is CandidValue.Blob -> TODO()
                            is CandidValue.Bool -> TODO()
                            CandidValue.Empty -> TODO()
                            is CandidValue.Float32 -> TODO()
                            is CandidValue.Float64 -> TODO()
                            is CandidValue.Function -> TODO()
                            is CandidValue.Integer -> TODO()
                            is CandidValue.Integer16 -> TODO()
                            is CandidValue.Integer32 -> TODO()
                            is CandidValue.Integer64 -> TODO()
                            is CandidValue.Integer8 -> TODO()
                            is CandidValue.Natural -> TODO()
                            is CandidValue.Natural16 -> TODO()
                            is CandidValue.Natural32 -> TODO()
                            is CandidValue.Natural64 -> TODO()
                            is CandidValue.Natural8 -> TODO()
                            CandidValue.Null -> TODO()
                            is CandidValue.Option -> TODO()
                            is CandidValue.Record -> {
                                *//**
                                 * buildObject(
                                 *                                 candidDictionary = value.dictionary,
                                 *                                 constructor = componentType.constructors.first()
                                 *                             )
                                 *//*
                                TODO()
                            }
                            CandidValue.Reserved -> TODO()
                            is CandidValue.Text -> TODO()
                            is CandidValue.Variant -> TODO()
                            is CandidValue.Vector -> TODO()
                            is CandidValue.Principal -> TODO()
                            is CandidValue.Service -> TODO()
                        }
                    }.toTypedArray()
                    arrayValues.copyInto(newArray, 0, 0, candidVector.values.size)
                    return newArray*/
                }
            }

            candidValue is CandidValue.Function -> {
                // TODO, null values to handle
                /**
                 * val name = candidValue.function.method?.name
                 *                 val principalId = ICPPrincipal.init(candidValue.function.method?.principalId!!)
                 *                 return (type.classifier as KClass<*>).primaryConstructor!!.call(name!!, principalId)
                 */
                TODO()
            }

            candidValue is CandidValue.Option -> {
                val value = candidValue.option.value ?: return null
                decode(candidValue = value, type = type)
            }

            candidValue is CandidValue.Variant -> {
                val sealedClass = type.jvmErasure
                require(sealedClass.isSealed) {
                    throw RuntimeException("Can't parse CandidVariant")
                }
                return sealedClass.nestedClasses.firstNotNullOfOrNull { nestedClass ->
                    try {
                        val constructor = nestedClass.primaryConstructor
                            ?: throw RuntimeException("No constructor for ${nestedClass.simpleName}")

                        when(candidValue.variant.value) {
                            is CandidValue.Blob -> TODO()
                            is CandidValue.Bool -> TODO()
                            CandidValue.Empty -> TODO()
                            is CandidValue.Float32 -> TODO()
                            is CandidValue.Float64 -> TODO()
                            is CandidValue.Function -> TODO()
                            is CandidValue.Integer -> TODO()
                            is CandidValue.Integer16 -> TODO()
                            is CandidValue.Integer32 -> TODO()
                            is CandidValue.Integer64 -> TODO()
                            is CandidValue.Integer8 -> TODO()
                            is CandidValue.Natural -> TODO()
                            is CandidValue.Natural16 -> TODO()
                            is CandidValue.Natural32 -> TODO()
                            is CandidValue.Natural64 -> TODO()
                            is CandidValue.Natural8 -> TODO()
                            CandidValue.Null -> TODO()
                            is CandidValue.Option -> TODO()
                            is CandidValue.Record -> {
                                /**
                                 * buildObject(
                                 *                                 candidDictionary = candidValue.variant.value.dictionary,
                                 *                                 constructor = constructor
                                 *                             )
                                 */
                                TODO()
                            }
                            CandidValue.Reserved -> TODO()
                            is CandidValue.Text -> TODO()
                            is CandidValue.Variant -> TODO()
                            is CandidValue.Vector -> TODO()
                            is CandidValue.Principal -> TODO()
                            is CandidValue.Service -> TODO()
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        println("Error for ${nestedClass.simpleName}")
                        null
                    }
                }
            }

            /**
             * buildObject(
             *                 candidDictionary = candidValue.dictionary,
             *                 constructor = (type.classifier as KClass<*>).primaryConstructor
             *                     ?: throw RuntimeException("Missing primary constructor")
             *             )
             */
            candidValue is CandidValue.Record -> TODO()

            else -> throw Exception("Unsupported type: $kClass for candid value: $candidValue")
        }
    }
}