package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidFileParser
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

class KotlinFileGeneratorTest {

    @Test
    fun test() {
        val filePath = "candid_file/tmp.did"
        val classLoader = this.javaClass.classLoader
        val file = File(classLoader.getResource(filePath)!!.file)
        assertTrue(file.exists())

        val idlFileDeclaration = CandidFileParser.parseFile(file.readText())
        val kotlinFileText = KotlinFileGenerator(idlFileDeclaration).getFileText()
        println(kotlinFileText)

        val kotlinFile = File("src/test/resources/generated_candid_file/tmp.kt")
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
    }
}