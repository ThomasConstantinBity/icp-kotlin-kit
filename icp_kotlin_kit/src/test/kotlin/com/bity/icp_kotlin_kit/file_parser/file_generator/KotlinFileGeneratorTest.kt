package com.bity.icp_kotlin_kit.file_parser.file_generator

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class KotlinFileGeneratorTest {

    @Test
    fun `parse file`() {
        val filePath = "src/test/resources/candid_file/nns_sns_w.did"
        val outputFilePath = "src/test/resources/generated_candid_file/nns_sns_w.kt"

    }

    companion object {

        @JvmStatic
        @BeforeAll
        fun `clear generated_candid_file folder`() {
            // val folder = File("src/test/resources/generated_candid_file")
            // folder.listFiles()?.forEach { it.deleteRecursively() }
        }
    }
}