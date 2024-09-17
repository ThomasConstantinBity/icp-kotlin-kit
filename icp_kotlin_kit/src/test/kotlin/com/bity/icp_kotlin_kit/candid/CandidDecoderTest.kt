package com.bity.icp_kotlin_kit.candid

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.candid.model.CandidVector
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class CandidDecoderTest {

    @ParameterizedTest
    @MethodSource("boolValue")
    fun `decode bool value`(
        candidValue: CandidValue,
        expectedResult: Boolean
    ) {
        assertEquals(
            expectedResult,
            CandidDecoder.decode(candidValue)
        )
    }

    @ParameterizedTest
    @MethodSource("optionValue")
    fun `decode option value`(
        candidValue: CandidValue,
        expectedResult: Any
    ) {
        assertEquals(
            expectedResult,
            CandidDecoder.decode(candidValue)
        )
    }

    @Disabled
    @Test
    fun `decode vector value`() {
        val vector = CandidValue.Vector(
            vector = CandidVector(
                listOf(CandidValue.Bool(true))
            )
        )
        val list = CandidDecoder.decode<Array<Boolean>>(vector)
        assertEquals(1, list?.size)
        assertTrue(list?.first() == true)
    }

    @Test
    fun `decode record value`() {

        data class QueryBlocksResponse(
            val chain_length: ULong
        )

        val candidValue = CandidValue.Record(
            dictionary = CandidDictionary(
                hashMapOf(
                    "chain_length" to CandidValue.Natural64(13786000UL)
                )
            )
        )
        val decoded = CandidDecoder.decode<QueryBlocksResponse>(candidValue)
        assertEquals(
            13786000UL,
            decoded?.chain_length
        )
    }

    companion object {

        @JvmStatic
        private fun boolValue() = listOf(
            Arguments.of(
                CandidValue.Bool(false),
                false
            ),
            Arguments.of(
                CandidValue.Bool(true),
                true
            )
        )

        @JvmStatic
        private fun optionValue() = listOf(
            Arguments.of(
                CandidValue.Option(
                    CandidValue.Bool(false)
                ),
                false
            ),
            Arguments.of(
                CandidValue.Option(
                    CandidValue.Option(
                        CandidValue.Bool(true)
                    )
                ),
                true
            )
        )
    }
}