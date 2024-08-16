package com.bity.icp_kotlin_kit.plugin.candid_parser

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLArgs
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.IDLValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

internal class CandidArgsParserTest {

    @Test
    fun `parse bool lit`() {
        val args = CandidArgsParser.parseArgs("(true)")
        assertEquals(
            expected = listOf(IDLBoolean(true)),
            actual = args.args
        )
    }

    @MethodSource("literals")
    @ParameterizedTest
    fun `parse string literals`(
        literal: String,
        expected: List<IDLValue>
    ) {
        val args = CandidArgsParser.parseArgs(literal)
        assertEquals(expected, args.args)
    }

    companion object {

        @JvmStatic
        private fun literals() = listOf(
            Arguments.of(
                " (true, null, 42, 42., +42.42, -42e5, 42.42e-5)",
                emptyList<IDLArgs>()
            ),
            /*Arguments.of(
                "(true, null, 4_2, \"哈哈\", \"string with whitespace\", 0x2a, -42, false)",
                emptyList<IDLArgs>()
            )*/
        )
    }
}