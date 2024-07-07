package com.bity.icp_candid

import com.bity.icp_candid.domain.deserializer.CandidDeserializer
import com.bity.icp_candid.domain.model.CandidDictionary
import com.bity.icp_candid.domain.model.CandidPrimitiveType
import com.bity.icp_candid.domain.model.CandidType
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_candid.domain.model.CandidVariant
import com.bity.icp_candid.domain.model.CandidVector
import com.bity.icp_candid.domain.serializer.CandidSerializer
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigInteger
import kotlin.test.DefaultAsserter

@OptIn(ExperimentalStdlibApi::class)
class CandidConversionTest {

    @ParameterizedTest
    @MethodSource("singleValues")
    fun `Single value conversion`(
        candidValue: CandidValue,
        expectedSerialized: ByteArray
    ) {
        val expected = CandidSerializer.magicBytes + expectedSerialized
        val encoded = CandidSerializer.encode(candidValue)
        println("[${candidValue.string}]: expected ${expected.toHexString()}, got ${encoded.toHexString()}")
        DefaultAsserter.assertTrue(
            "Error while encoding ${candidValue.string}",
            expected.contentEquals(encoded)
        )
        val decoded = CandidDeserializer.decode(encoded)
        DefaultAsserter.assertEquals(
            "Error while decoding ${candidValue.string}",
            candidValue,
            decoded.first()
        )
    }

    @ParameterizedTest
    @MethodSource("multipleValues")
    fun `Multiple values conversion`(
        candidValues: List<CandidValue>,
        expectedSerialized: ByteArray
    ) {
        val encoded = CandidSerializer.encode(candidValues)
        val expected = CandidSerializer.magicBytes + expectedSerialized
        DefaultAsserter.assertTrue(
            "Error while decoding ${candidValues.forEach { it.string }}. " +
                    "Expected: ${expected.toHexString()}, got: ${encoded.toHexString()}",
            expected.contentEquals(encoded)
        )
        val decoded = CandidDeserializer.decode(encoded)
        DefaultAsserter.assertEquals(
            message = null,
            expected = candidValues,
            actual = decoded
        )
    }

    private val CandidValue.string: String
        get() = when(this) {
            is CandidValue.Null -> "[Null]"
            is CandidValue.Blob -> "[Blob]: ${data.toHexString()}"
            is CandidValue.Bool -> "[Bool]: $bool"
            CandidValue.Empty -> "[Empty]"
            is CandidValue.Float32 -> "[Float32]: $float"
            is CandidValue.Float64 -> "[Float64]: $double"
            is CandidValue.Function -> "[Function]"
            is CandidValue.Integer -> "[Integer]: $bigInt"
            is CandidValue.Integer16 -> "[Integer16]: $int16"
            is CandidValue.Integer32 -> "[Integer32]:$int32"
            is CandidValue.Integer64 -> "[Integer64]: $int64"
            is CandidValue.Integer8 -> "[Integer8]: $int8"
            is CandidValue.Natural -> "[Natural]: $bigUInt"
            is CandidValue.Natural16 -> "[Natural16]: $uInt16"
            is CandidValue.Natural32 -> "[Natural32]: $uInt32"
            is CandidValue.Natural64 -> "[Natural64]: $uInt64"
            is CandidValue.Natural8 -> "[Natural8]: $uInt8"
            is CandidValue.Option -> "[Option]"
            is CandidValue.Record -> "[Record]: $dictionary"
            CandidValue.Reserved -> "[Reserved]"
            is CandidValue.Text -> "[Text]: $string"
            is CandidValue.Variant -> "[Variant]"
            is CandidValue.Vector -> "[Vector]"
        }

    companion object {
        @JvmStatic
        fun singleValues() = listOf(
            arrayOf(CandidValue.Null, byteArrayOf(0x00, 0x01, 0x7F)),
            arrayOf(CandidValue.Bool(false), byteArrayOf(0x00, 0x01, 0x7E, 0x00)),
            arrayOf(CandidValue.Bool(true), byteArrayOf(0x00, 0x01, 0x7E, 0x01)),
            arrayOf(CandidValue.Natural(BigInteger.ZERO), byteArrayOf(0x00, 0x01, 0x7D, 0x0)),
            arrayOf(CandidValue.Natural(BigInteger.ONE), byteArrayOf(0x00, 0x01, 0x7D, 0x01)),
            arrayOf(
                CandidValue.Natural(BigInteger.valueOf(300)), byteArrayOf(
                    0x00, 0x01, 0x7D, 0xAC.toByte(), 0x02
                )
            ),
            arrayOf(
                CandidValue.Integer(BigInteger.valueOf(-129)), byteArrayOf(
                    0x00, 0x01, 0x7C, 0xFF.toByte(), 0x7E
                )
            ),
            arrayOf(CandidValue.Natural8(5.toUByte()), byteArrayOf(0x00, 0x01, 0x7B, 0x05)),
            arrayOf(CandidValue.Natural16(5.toUShort()), byteArrayOf(0x00, 0x01, 0x7A, 0x05, 0x00)),
            arrayOf(
                CandidValue.Natural32(5.toUInt()), byteArrayOf(0x00, 0x01, 0x79, 0x05, 0x00, 0x00, 0x00)
            ),
            arrayOf(
                CandidValue.Natural64(5.toULong()), byteArrayOf(
                    0x00, 0x01, 0x78, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                )
            ),
            arrayOf(CandidValue.Integer8(-5), byteArrayOf(0x00, 0x01, 0x77, 0xfb.toByte())),
            arrayOf(
                CandidValue.Integer16(-5), byteArrayOf(0x00, 0x01, 0x76, 0xfB.toByte(), 0xff.toByte())
            ),
            arrayOf(
                CandidValue.Integer32(-5), byteArrayOf(
                    0x00, 0x01, 0x75, 0xfB.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte()
                )
            ),
            arrayOf(
                CandidValue.Integer64(-5), byteArrayOf(
                    0x00,
                    0x01,
                    0x74,
                    0xfB.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte()
                )
            ),
            arrayOf(
                CandidValue.Float32(-0.5F), byteArrayOf(
                    0x00, 0x01, 0x73, 0x00, 0x00, 0x00, 0xbf.toByte()
                )
            ),
            arrayOf(
                CandidValue.Float32(-0.768F), byteArrayOf(
                    0x00, 0x01, 0x73, 0xa6.toByte(), 0x9b.toByte(), 0x44, 0xbf.toByte()
                )
            ),
            arrayOf(
                CandidValue.Float64(-0.768), byteArrayOf(
                    0x00,
                    0x01,
                    0x72,
                    0xFA.toByte(),
                    0x7E,
                    0x6A,
                    0xBC.toByte(),
                    0x74,
                    0x93.toByte(),
                    0xE8.toByte(),
                    0xBF.toByte()
                )
            ),
            arrayOf(CandidValue.Reserved, byteArrayOf(0x00, 0x01, 0x70)),
            arrayOf(CandidValue.Empty, byteArrayOf(0x00, 0x01, 0x6F)),
            arrayOf(CandidValue.Text("a"), byteArrayOf(0x00, 0x01, 0x71, 0x01, 0x61)),
            arrayOf(
                CandidValue.Text("%±§"), byteArrayOf(
                    0x00,
                    0x01,
                    0x71,
                    0x05,
                    0x25,
                    0xc2.toByte(),
                    0xb1.toByte(),
                    0xc2.toByte(),
                    0xa7.toByte()
                )
            ),
            arrayOf(// 1 type in table, option, bool, 1 candidValue, value of type 0, null value
                CandidValue.Option(CandidPrimitiveType.BOOL), byteArrayOf(
                    0x01, 0x6E, 0x7E, 0x01, 0x00, 0x00
                )
            ),
            arrayOf(// 1 type in table, option, bool, 1 candidValue, value of type 0, non-null value, true
                CandidValue.Option(CandidValue.Bool(true)), byteArrayOf(
                    0x01, 0x6E, 0x7E, 0x01, 0x00, 0x01, 0x01
                )
            ),
            arrayOf(// 1 type in table, vector, bool, 1 candidValue, value of type 0, 0 elements
                CandidValue.Vector(CandidPrimitiveType.BOOL), byteArrayOf(
                    0x01, 0x6D, 0x7E, 0x01, 0x00, 0x00
                )
            ),
            arrayOf(// 1 type in table, vector, bool, 1 candidValue, value of type 0, 2 elements, true, false
                CandidValue.Vector(
                    CandidVector(
                        listOf(
                            CandidValue.Bool(true), CandidValue.Bool(false)
                        )
                    )
                ), byteArrayOf(0x01, 0x6D, 0x7E, 0x01, 0x00, 0x02, 0x01, 0x00)
            ),
            arrayOf(// 1 type in table, vector, nat8, 1 candidValue, value of type 0, 0 elements
                CandidValue.Blob(byteArrayOf()), byteArrayOf(0x01, 0x6D, 0x7B, 0x01, 0x00, 0x00)
            ),
            arrayOf(// 1 type in table, vector, nat8, 1 candidValue, value of type 0, 2 elements, 127, 128
                CandidValue.Blob(
                    byteArrayOf(127.toByte(), 128.toByte())
                ), byteArrayOf(0x01, 0x6D, 0x7B, 0x01, 0x00, 0x02, 0x7F, 0x80.toByte())
            ),
            arrayOf(// 1 type in table, record, 0 rows, 1 candidValue, value of type 0,
                CandidValue.Record(CandidDictionary(emptyMap())),
                byteArrayOf(0x01, 0x6C, 0x00, 0x01, 0x00)
            ),
            arrayOf(// 1 type in table, record, 1 row, leb(hash("a")), .empty, 1 candidValue, value of type 0,
                CandidValue.Record(
                    CandidDictionary(
                        hashMapOf("a" to CandidValue.Empty)
                    )
                ), byteArrayOf(0x01, 0x6C, 0x01, 97, 0x6F, 0x01, 0x00)
            ),
            arrayOf(// 1 type in table, record, 2 rows, leb(hash("a")), .natural, leb(hash("b")), .natural8, 1 candidValue, value of type 0, 0x01, 0x02
                CandidValue.Record(
                    CandidDictionary(
                        hashMapOf(
                            "a" to CandidValue.Natural(BigInteger.valueOf(1)),
                            "b" to CandidValue.Natural8(2U)
                        )
                    )
                ), byteArrayOf(0x01, 0x6C, 0x02, 97, 0x7D, 98, 0x7B, 0x01, 0x00, 0x01, 0x02)
            ),
            arrayOf(// 2 types in table, (0)vector, bool, (1)option, referencing type 0, 1 candidValue, value of type 1, option present, 2 values, true, false
                CandidValue.Option(
                    CandidValue.Vector(
                        CandidVector(
                            listOf(
                                CandidValue.Bool(true), CandidValue.Bool(false)
                            )
                        )
                    )
                ), byteArrayOf(0x02, 0x6D, 0x7E, 0x6E, 0x00, 0x01, 0x01, 0x01, 0x02, 0x01, 0x00)
            ),
            arrayOf(// 3 types in table, (0)vector, nat8, (1) vector, ref 0, (2)option, ref 1, 1 candidValue, value of type 2, option present, 2 values, length 0, length 2, leb(127), leb(128)
                CandidValue.Option(
                    CandidValue.Vector(
                        CandidVector(
                            listOf(
                                CandidValue.Blob(byteArrayOf()),
                                CandidValue.Blob(byteArrayOf(127.toByte(), 128.toByte()))
                            )
                        )
                    )
                ), byteArrayOf(
                    0x03,
                    0x6D,
                    0x7B,
                    0x6D,
                    0x00,
                    0x6E,
                    0x01,
                    0x01,
                    0x02,
                    0x01,
                    0x02,
                    0x00,
                    0x02,
                    0x7F,
                    0x80.toByte()
                )
            ),
            arrayOf(// 4 types in table, (0)vector, nat8, (1) vector, ref 0, (2) record, 2 keys, leb(hash("a")), ref 0, leb(hash("b")), ref 1, (3)option, ref 2, 1 candidValue, value of type 3, option present, length 1, 0x44, length 1, length 2, 0x45, 0x47
                CandidValue.Option(
                    CandidValue.Record(
                        CandidDictionary(
                            hashMapOf(
                                "a" to CandidValue.Blob(byteArrayOf(0x44)), "b" to CandidValue.Vector(
                                    CandidVector(
                                        listOf(CandidValue.Blob(byteArrayOf(0x45, 0x47)))
                                    )
                                )
                            )
                        )
                    )
                ), byteArrayOf(
                    4,
                    0x6D,
                    0x7B,
                    0x6D,
                    0,
                    0x6C,
                    2,
                    97,
                    0,
                    98,
                    1,
                    0x6E,
                    2,
                    0x01,
                    3,
                    1,
                    1,
                    0x44,
                    1,
                    2,
                    0x45,
                    0x47
                )
            ),
            arrayOf(// 2 types in table, (0) vector, nat8, (1) variant, 3 keys, leb(hash("a")), bool, leb(hash("b")), nat8, leb(hash("c")), type 0, 1 candidValue, type 1, row 1, 15
                CandidValue.Variant(
                    CandidVariant(
                        hashMapOf(
                            "a" to CandidType.Primitive(CandidPrimitiveType.BOOL),
                            "b" to CandidType.Primitive(CandidPrimitiveType.NATURAL8),
                            "c" to CandidType.Container(
                                CandidPrimitiveType.VECTOR,
                                CandidType.Primitive(CandidPrimitiveType.NATURAL8)
                            )
                        ), Pair("b", CandidValue.Natural8(15U))
                    )
                ), byteArrayOf(2, 0x6D, 0x7B, 0x6B, 3, 97, 0x7E, 98, 0x7B, 99, 0, 1, 1, 1, 0x0f)
            )
        )

        @JvmStatic
        fun multipleValues() = listOf(
            arrayOf(
                emptyList<CandidValue>(),
                byteArrayOf(0x00, 0x00)
            ),
            arrayOf(
                listOf(
                    CandidValue.Natural8(0U),
                    CandidValue.Natural8(1U),
                    CandidValue.Natural8(2U)
                ), byteArrayOf(0x00, 0x03, 0x7B, 0x7B, 0x7B, 0, 1, 2)
            ),
            arrayOf(
                listOf(
                    CandidValue.Natural8(0U),
                    CandidValue.Natural16(258.toUShort()),
                    CandidValue.Natural8(2U)
                ), byteArrayOf(0x00, 0x03, 0x7B, 0x7A, 0x7B, 0, 2, 1, 2)
            ),
            // 4 types in table, (0)vector, nat8, (1) vector, ref 0, (2) record, 2 keys, leb(hash("a")), ref 0, leb(hash("b")), ref 1, (3)option, ref 2, 2 candidValues, value of type 3, value of type 2, option present, length 1, 0x44, length 1, length 2, 0x45, 0x47,  length 1, 0x43, length 1, length 2, 0x40, 0x41
            arrayOf(
                listOf(
                    // First Item
                    CandidValue.Option(
                        CandidValue.Record(
                            CandidDictionary(
                                hashMapOf(
                                    "a" to CandidValue.Blob(byteArrayOf(0x44)),
                                    "b" to CandidValue.Vector(
                                        CandidVector(
                                            listOf(
                                                CandidValue.Blob(byteArrayOf(0x45, 0x47))
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ),
                    // Second Item
                    CandidValue.Record(
                        CandidDictionary(
                            hashMapOf(
                                "a" to CandidValue.Blob(byteArrayOf(0x43)),
                                "b" to CandidValue.Vector(
                                    CandidVector(
                                        listOf(
                                            CandidValue.Blob(byteArrayOf(0x40, 0x41))
                                        )
                                    )
                                )
                            )
                        )
                    )
                ), byteArrayOf(4, 0x6D, 0x7B, 0x6D, 0, 0x6C, 2, 97, 0, 98, 1, 0x6E, 2, 0x02, 3, 2, 1, 1, 0x44, 1, 2, 0x45, 0x47, 1, 0x43, 1, 2, 0x40, 0x41)
            )
        )
    }
}