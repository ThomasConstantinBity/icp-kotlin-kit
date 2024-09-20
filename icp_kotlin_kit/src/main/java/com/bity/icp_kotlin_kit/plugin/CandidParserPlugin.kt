package com.bity.icp_kotlin_kit.plugin

/*
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
*/

/*
class CandidParserPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        println("HELLO")
        val extension = target.extensions.create("candidParser", CandidParserExtension::class.java)
        println("Got ext: ${extension.inputPath.get()}")

        target.afterEvaluate {
            val folderPath = extension.inputPath.get()
            val folder = File(folderPath)

            if (!folder.exists() || !folder.isDirectory) {
                throw IllegalArgumentException("The provided path '$folderPath' is not a valid directory.")
            }

            println("Reading files from folder: ${folder.absolutePath}")

            // Read and parse files in the directory
            folder.listFiles()?.forEach { file ->
                if (file.isFile) {
                    println("Parsing file: ${file.name}")
                    val content = file.readText()
                    // Parse the file content as needed
                    // parseFile(content)
                }
            }
        }
    }

}*/
