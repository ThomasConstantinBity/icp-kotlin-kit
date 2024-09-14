package com.bity.icp_kotlin_kit.plugin.file_generator

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class KotlinFileGeneratorTest {

    @ParameterizedTest(name = "[{index}] - parsing {0}")
    @MethodSource("filePaths")
    fun `parse files`(
        filePath: String,
        outputFilePath: String
    ) {
        KotlinFileGenerator(
            didFilePath = filePath,
            outputFilePath = outputFilePath
        ).generateKotlinFile()
    }

    companion object {

        @JvmStatic
        @BeforeAll
        fun `clear generated_candid_file folder`() {
            // val folder = File("src/test/resources/generated_candid_file")
            // folder.listFiles()?.forEach { it.deleteRecursively() }
        }

        @JvmStatic
        private fun filePaths() = listOf(
            /*Arguments.of(
                "src/test/resources/candid_file/LedgerCanister.did",
                "src/test/resources/generated_candid_file/LedgerCanister.kt"
            ),*/
            Arguments.of(
                "src/test/resources/candid_file/ICRC7.did",
                "src/test/resources/generated_candid_file/ICRC7.kt"
            )
        )
    }
}