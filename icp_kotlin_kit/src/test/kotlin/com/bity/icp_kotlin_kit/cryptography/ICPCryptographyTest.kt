package com.bity.icp_kotlin_kit.cryptography

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ICPCryptographyTest {

    @ParameterizedTest(name = "Encoding {1}")
    @MethodSource("encodeCanonicalTextTestValues")
    fun encodeCanonicalText(
        data: ByteArray,
        expectedResult: String
    ) {
        assertEquals(
            expected = expectedResult,
            actual = ICPCryptography.encodeCanonicalText(data)
        )
    }

    @ParameterizedTest(name = "Decoding {0}")
    @MethodSource("decodeCanonicalTextTestValues")
    fun decodeCanonicalText(
        stringValue: String,
        expectedResult: ByteArray
    ) {
        val decoded = ICPCryptography.decodeCanonicalText(stringValue)
        assertTrue(expectedResult.contentEquals(decoded))
    }

    companion object {

        @JvmStatic
        @OptIn(ExperimentalStdlibApi::class)
        // Generated using https://internetcomputer.org/docs/current/references/id-encoding-spec#test-vectors
        private fun encodeCanonicalTextTestValues() = listOf(
            Arguments.of("000102030405060708".hexToByteArray(), "xtqug-aqaae-bagba-faydq-q"),
            Arguments.of("00".hexToByteArray(), "2ibo7-dia"),
            Arguments.of("".hexToByteArray(), "aaaaa-aa"),
            Arguments.of(
                "0102030405060708091011121314151617181920212223242526272829".hexToByteArray(),
                "iineg-fibai-bqibi-ga4ea-searc-ijrif-iwc4m-bsibb-eirsi-jjge4-ucs"
            )
        )

        @JvmStatic
        @OptIn(ExperimentalStdlibApi::class)
        // Generated using https://internetcomputer.org/docs/current/references/id-encoding-spec#test-vectors
        private fun decodeCanonicalTextTestValues() = listOf(
            Arguments.of("ryjl3-tyaaa-aaaaa-aaaba-cai", "00000000000000020101".hexToByteArray()),
            Arguments.of("xtqug-aqaae-bagba-faydq-q", "000102030405060708".hexToByteArray()),
            Arguments.of("2ibo7-dia", "00".hexToByteArray()),
            Arguments.of("2IBO7-DIA", "00".hexToByteArray()),
            Arguments.of("2Ibo7-diA", "00".hexToByteArray()),
            Arguments.of("w3gef-eqbai", "0102".hexToByteArray()),
        )

    }
}