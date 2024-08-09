package com.bity.candid_parser

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class CandidParserPluginTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `plugin should validate and print the folder path`() {
        // Setup the build.gradle.kts file in the tempDir
        val buildFile = File(tempDir, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("com.bity.candid_parser")
            }

            candidParser {
                inputPath.set("${'$'}{rootDir}/src")
            }
        """.trimIndent())

        // Create the folder to simulate an existing directory
        File(tempDir, "src").mkdirs()

        // Run the plugin
        val result = GradleRunner.create()
            .withProjectDir(tempDir)
            .withArguments("help", "--stacktrace")
            .withPluginClasspath()
            .build()

        // Verify the output
        assertTrue(result.output.contains("The provided folder path is: ${tempDir.absolutePath}/src"))
    }

    @Test
    fun `plugin should fail for invalid folder path`() {
        // Setup the build.gradle.kts file in the tempDir
        val buildFile = File(tempDir, "build.gradle.kts")
        buildFile.writeText("""
            plugins {
                id("com.example.custom-plugin")
            }

            customPluginExtension {
                folderPath.set("${'$'}{rootDir}/nonexistent")  // Provide an invalid folder path
            }
        """.trimIndent())

        // Run the plugin and expect failure
        val result = GradleRunner.create()
            .withProjectDir(tempDir)
            .withArguments("help", "--stacktrace")
            .withPluginClasspath()
            .buildAndFail()

        // Verify the output contains the error message
        assertTrue(result.output.contains("The provided path '${tempDir.absolutePath}/nonexistent' is not a valid directory."))
    }
}