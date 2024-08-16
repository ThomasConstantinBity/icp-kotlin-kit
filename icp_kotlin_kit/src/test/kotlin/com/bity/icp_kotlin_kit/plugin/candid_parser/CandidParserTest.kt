package com.bity.icp_kotlin_kit.plugin.candid_parser

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertTrue

private class CandidParserTest {

    @MethodSource("candidFilePath")
    @ParameterizedTest
    fun `parse did files`(filePath: String) {
        val classLoader = this.javaClass.classLoader
        val file = File(classLoader.getResource(filePath)!!.file)
        assertTrue(file.exists())
        CandidParser.parse(file.readText())
        TODO()
    }

    companion object {

        @JvmStatic
        private fun candidFilePath() = listOf(
            Arguments.of("candid_file/LedgerCanister.did")
        )
    }
}