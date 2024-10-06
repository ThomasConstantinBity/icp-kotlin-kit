package com.bity.icp_kotlin_kit.file_parser

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CandidParserTask: DefaultTask() {

    @Input
    var inputFolderPath: String? = null

    @Input
    var outputFolderPath: String? = null

    @TaskAction
    fun execute() {
        requireNotNull(inputFolderPath)
        requireNotNull(outputFolderPath)

        logger.info("Parsing files from $inputFolderPath")
        val inputFolder = File(inputFolderPath!!)
        require(inputFolder.isDirectory) {
            logger.error("$inputFolder is not a directory")
            return
        }
        inputFolder.walkTopDown().forEach { file ->
            if(file.extension == "did") {
                logger.info("Parsing file ${file.name}")
            }
        }
    }
}
