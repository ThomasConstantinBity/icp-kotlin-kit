package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidFileParser
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertTrue

class KotlinFileGeneratorTest {

    @ParameterizedTest(name = "[{index}] - parsing {0}")
    @MethodSource("filePaths")
    fun `parse files`(
        filePath: String,
        outputFilePath: String
    ) {
        val fileName = filePath.split("/")
            .last()
            .removeSuffix(".did")
        val classLoader = this.javaClass.classLoader
        val file = File(classLoader.getResource(filePath)!!.file)
        assertTrue(file.exists())

        val idlFileDeclaration = CandidFileParser.parseFile(file.readText())
        val kotlinFileText = KotlinFileGenerator.generateFileText(idlFileDeclaration, fileName)

        val kotlinFile = File(outputFilePath)
        kotlinFile.createNewFile()
        kotlinFile.writeText(kotlinFileText)
    }

    companion object {

        @JvmStatic
        @BeforeAll
        fun `clear generated_candid_file folder`() {
            val folder = File("src/test/resources/generated_candid_file")
            folder.listFiles()?.forEach { it.deleteRecursively() }
        }

        @JvmStatic
        private fun filePaths() = listOf(

            Arguments.of(
                "candid_file/ICRC7.did",
                "src/test/resources/generated_candid_file/ICRC7.kt"
            )
        )
    }
}