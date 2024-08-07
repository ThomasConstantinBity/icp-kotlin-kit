package com.bity.icp_kotlin_kit.candid.deserializer

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidFunction
import com.bity.icp_kotlin_kit.candid.model.CandidPrimitiveType
import com.bity.icp_kotlin_kit.candid.model.CandidType
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.candid.model.CandidVariant
import com.bity.icp_kotlin_kit.candid.model.CandidVector
import com.bity.icp_kotlin_kit.candid.serializer.CandidSerializer
import com.bity.icp_kotlin_kit.cryptography.LEB128
import com.bity.icp_kotlin_kit.data.model.CandidDeserializationError
import com.bity.icp_kotlin_kit.util.ext_function.readFrom
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.UnsupportedOperationException

internal object CandidDeserializer {
    @Throws(
        CandidDeserializationError.InvalidPrefix::class,
        CandidDeserializationError.InvalidUTF8String::class,
        CandidDeserializationError.InvalidTypeReference::class,
        CandidDeserializationError.UnSerializedBytesLeft::class,
    )
    fun decode(data: ByteArray): List<CandidValue> {
        require(
            data
                .take(CandidSerializer.magicBytes.size).toByteArray()
                .contentEquals(CandidSerializer.magicBytes)
        ) {
            throw CandidDeserializationError.InvalidPrefix()
        }
        val unwrappedData = data.drop(CandidSerializer.magicBytes.size).toByteArray()
        val stream = ByteArrayInputStream(unwrappedData)
        val typeTable = CandidDecodableTypeTable(stream)
        val nCandidValues: Int = LEB128.decodeUnsigned(stream)
        val decodedTypes = (0 until nCandidValues).map {
            val typeRef: Int = LEB128.decodeSigned(stream)
            val candidType = typeTable.getTypeForReference(typeRef)
            candidType
        }
        val decodedValues = decodedTypes.map { candidType ->
            val decodedValue = decodeValue(candidType, stream)
            decodedValue
        }

        require(stream.available() == 0) {
            throw CandidDeserializationError.UnSerializedBytesLeft()
        }
        return decodedValues
    }

    @Throws(
        CandidDeserializationError.InvalidUTF8String::class,
        CandidDeserializationError.InvalidTypeReference::class
    )
    private fun decodeValue(
        type: CandidType,
        stream: InputStream
    ): CandidValue = when(type.primitiveType) {
        CandidPrimitiveType.NULL ->
            CandidValue.Null
        CandidPrimitiveType.BOOL ->
            CandidValue.Bool(stream.read() != 0)
        CandidPrimitiveType.NATURAL ->
            CandidValue.Natural(LEB128.decodeUnsigned(stream))
        CandidPrimitiveType.INTEGER ->
            CandidValue.Integer(LEB128.decodeSigned(stream))
        CandidPrimitiveType.NATURAL8 -> CandidValue.Natural8(UByte.readFrom(stream))
        CandidPrimitiveType.NATURAL16 -> CandidValue.Natural16(UShort.readFrom(stream))
        CandidPrimitiveType.NATURAL32 -> CandidValue.Natural32(UInt.readFrom(stream))
        CandidPrimitiveType.NATURAL64 -> CandidValue.Natural64(ULong.readFrom(stream))
        CandidPrimitiveType.INTEGER8 -> CandidValue.Integer8(Byte.readFrom(stream))
        CandidPrimitiveType.INTEGER16 -> CandidValue.Integer16(Short.readFrom(stream))
        CandidPrimitiveType.INTEGER32 -> CandidValue.Integer32(Int.readFrom(stream))
        CandidPrimitiveType.INTEGER64 -> CandidValue.Integer64(Long.readFrom(stream))
        CandidPrimitiveType.FLOAT32 -> CandidValue.Float32(Float.readFrom(stream))
        CandidPrimitiveType.FLOAT64 -> CandidValue.Float64(Double.readFrom(stream))
        CandidPrimitiveType.TEXT -> {
            val string = readStringFromInputStream(stream)
            CandidValue.Text(string)
        }
        CandidPrimitiveType.FUNCTION -> {
            val isPresent = stream.read() == 1
            val serviceMethod: CandidFunction.ServiceMethod? = if(isPresent) {
                require(stream.read() == 1) {
                    throw CandidDeserializationError.InvalidTypeReference()
                }
                val principalIdLength: Int = LEB128.decodeUnsigned(stream)
                val principalId = ByteArray(principalIdLength)
                stream.read(principalId, 0, principalIdLength)
                val name = readStringFromInputStream(stream)
                CandidFunction.ServiceMethod(
                    name = name,
                    principalId = principalId
                )
            } else null
            CandidValue.Function(
                CandidFunction(
                    signature = type.functionSignature!!,
                    method = serviceMethod
                )
            )

        }
        CandidPrimitiveType.RESERVED ->
            CandidValue.Reserved
        CandidPrimitiveType.EMPTY ->
            CandidValue.Empty
        CandidPrimitiveType.OPTION -> {
            val containedType = type.containedType!!
            val isPresent = stream.read() == 1
            if(isPresent) {
                val value = decodeValue(containedType, stream)
                CandidValue.Option(value)
            } else {
                CandidValue.Option(containedType)
            }
        }
        CandidPrimitiveType.VECTOR -> {
            val containedType = type.containedType!!
            val nItems: Int = LEB128.decodeUnsigned(stream)
            val items = (0 until nItems).map {
                decodeValue(containedType, stream)
            }
            // special handling of vector(nat8). We convert them to blob(Data)
            if(containedType.primitiveType == CandidPrimitiveType.NATURAL8) {
                // I can convert BigInt to byte because primitiveType is Natural8, the value is 1 byte
                val data = items.map { it.natural8Value!!.toByte() }.toByteArray()
                CandidValue.Blob(data)
            } else {
                CandidValue.Vector(CandidVector(containedType, items))
            }
        }
        CandidPrimitiveType.RECORD -> {
            val rowTypes = type.keyedContainerRowTypes!!
            val dictionary = hashMapOf<ULong, CandidValue>()
            rowTypes.forEach {
                dictionary[it.hashedKey] = decodeValue(it.type, stream)
            }
            CandidValue.Record(CandidDictionary(dictionary))
        }
        CandidPrimitiveType.VARIANT -> {
            val rowTypes = type.keyedContainerRowTypes!!
            val valueIndex: ULong = LEB128.decodeUnsigned(stream)
            CandidValue.Variant(
                CandidVariant(
                    candidTypes = rowTypes,
                    value = decodeValue(rowTypes[valueIndex.toInt()].type, stream),
                    valueIndex = valueIndex
                )
            )
        }
    }

    @Throws(CandidDeserializationError.InvalidUTF8String::class)
    private fun readStringFromInputStream(inputStream: InputStream): String {
        val length: Int = LEB128.decodeUnsigned(inputStream)
        val data = ByteArray(length)
        inputStream.read(data, 0, length)
        return try {
            String(data, Charsets.UTF_8)
        } catch (_: UnsupportedOperationException) {
            throw CandidDeserializationError.InvalidUTF8String()
        }
    }
}