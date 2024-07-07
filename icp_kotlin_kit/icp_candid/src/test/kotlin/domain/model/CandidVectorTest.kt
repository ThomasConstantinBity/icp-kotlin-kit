package com.bity.icp_candid.domain.model

import com.bity.icp_candid.domain.model.error.CandidVectorError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CandidVectorTest {

    @Test
    fun `constructor with type and sequence`() {
        val type = CandidType.Primitive(CandidPrimitiveType.EMPTY)
        val candidVector = CandidVector(type, listOf(CandidValue.Empty))
        assertEquals(type, candidVector.containedType)
        assertTrue(candidVector.values.isNotEmpty())
    }

    @Test
    fun `constructor with Container type and sequence`() {
        val type = CandidType.Container(
            CandidPrimitiveType.OPTION,
            CandidType.Primitive(CandidPrimitiveType.BOOL)
        )
        val candidVector = CandidVector(
            type,
            listOf(
                CandidValue.Option(
                    CandidOption.Some(CandidValue.Bool(true))
                )
            )
        )
        assertEquals(type, candidVector.containedType)
        assertTrue(candidVector.values.isNotEmpty())
    }

    @Test
    fun `constructor with wrong Container type and sequence`() {
        val type = CandidType.Container(
            CandidPrimitiveType.OPTION,
            CandidType.Primitive(CandidPrimitiveType.BOOL)
        )
        org.junit.jupiter.api.assertThrows<CandidVectorError.WrongCandidType> {
            CandidVector(
                type,
                listOf(
                    CandidValue.Option(CandidOption.Some(CandidValue.Bool(true))),
                    CandidValue.Bool(false)
                )
            )
        }
    }

    @Test
    fun `constructor with Function type and sequence`() {
        val functionSignature = CandidFunction.CandidFunctionSignature(
            emptyList(),
            emptyList(),
            isQuery = false,
            isOneWay = false
        )
        val type = CandidType.Function(functionSignature)
        val candidVector = CandidVector(
            type,
            listOf(
                CandidValue.Function(CandidFunction(functionSignature, null))
            )
        )
        assertEquals(type, candidVector.containedType)
        assertTrue(candidVector.values.isNotEmpty())
    }

    @Test
    fun `constructor with wrong Function type and sequence`() {
        val functionSignature = CandidFunction.CandidFunctionSignature(
            emptyList(),
            emptyList(),
            isQuery = false,
            isOneWay = false
        )
        val wrongFunctionSignature = CandidFunction.CandidFunctionSignature(
            listOf(CandidType.Primitive(CandidPrimitiveType.BOOL)),
            emptyList(),
            isQuery = false,
            isOneWay = false
        )
        val type = CandidType.Function(functionSignature)
        org.junit.jupiter.api.assertThrows<CandidVectorError.WrongCandidType> {
            CandidVector(
                type,
                listOf(
                    CandidValue.Function(CandidFunction(wrongFunctionSignature, null))
                )
            )
        }
    }

    @Test
    fun `constructor with KeyedContainer type and sequence`() {
        val candidDictionaryItemType = CandidDictionaryItemType(
            0UL,
            CandidType.Primitive(CandidPrimitiveType.BOOL)
        )
        val type = CandidType.KeyedContainer(
            primitiveType = CandidPrimitiveType.RECORD,
            dictionaryItemType = listOf(candidDictionaryItemType)
        )
        val candidVector = CandidVector(
            type,
            listOf(
                CandidValue.Record(
                    CandidDictionary(
                        hashMapOf(0UL to CandidValue.Bool(false))
                    )
                )
            )
        )
        assertEquals(type, candidVector.containedType)
        assertTrue(candidVector.values.isNotEmpty())
        assertEquals(type, candidVector.values.first().candidType)
    }

    @Test
    fun `constructor with wrong KeyedContainer type and sequence`() {
        val candidDictionaryItemType = CandidDictionaryItemType(
            0UL,
            CandidType.Primitive(CandidPrimitiveType.BOOL)
        )
        val type = CandidType.KeyedContainer(
            primitiveType = CandidPrimitiveType.RECORD,
            dictionaryItemType = listOf(candidDictionaryItemType)
        )
        org.junit.jupiter.api.assertThrows<CandidVectorError.WrongCandidType> {
            CandidVector(
                type,
                listOf(
                    CandidValue.Record(
                        CandidDictionary(
                            hashMapOf(0UL to CandidValue.Bool(false))
                        )
                    ),
                    CandidValue.Record(
                        CandidDictionary(
                            hashMapOf(0UL to CandidValue.Bool(false))
                        )
                    ),
                    CandidValue.Record(
                        CandidDictionary(
                            hashMapOf(0UL to CandidValue.Null)
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `constructor with KeyedContainer type different dictionary`() {
        val candidDictionaryItemType = CandidDictionaryItemType(
            0UL,
            CandidType.Primitive(CandidPrimitiveType.BOOL)
        )
        val wrongCandidDictionaryItemType =  CandidDictionaryItemType(
            1UL,
            CandidType.Primitive(CandidPrimitiveType.NULL)
        )
        val type = CandidType.KeyedContainer(
            primitiveType = CandidPrimitiveType.RECORD,
            dictionaryItemType = listOf(
                candidDictionaryItemType,
                wrongCandidDictionaryItemType
            )
        )
        org.junit.jupiter.api.assertThrows<CandidVectorError.WrongCandidType> {
            CandidVector(
                type,
                listOf(
                    CandidValue.Record(
                        CandidDictionary(
                            hashMapOf(0UL to CandidValue.Bool(false))
                        )
                    ),
                    CandidValue.Record(
                        CandidDictionary(
                            hashMapOf(0UL to CandidValue.Null)
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `constructor with wrong candid type`() {
        val type = CandidType.Primitive(CandidPrimitiveType.EMPTY)
        org.junit.jupiter.api.assertThrows<CandidVectorError.WrongCandidType> {
            CandidVector(type, listOf(CandidValue.Bool(false)))
        }
    }
}