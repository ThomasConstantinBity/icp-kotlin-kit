package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidFileParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinitionType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinTypeDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.toKotlinFileString
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.CandidDefinitionHelper
import java.io.File
import java.lang.IllegalStateException

internal class KotlinFileGenerator(
    private val didFilePath: String,
    private val outputFilePath: String,
    private val showCandidDefinition: Boolean = true,
    private val removeCandidComment: Boolean = false
) {

    private val fileName = didFilePath.split("/")
        .last()
        .removeSuffix(".did")

    private val fileText = StringBuilder(HEADER).appendLine()

    fun generateKotlinFile() {

        val inputFile = File(didFilePath)
        val outputFile = File(outputFilePath)
        assert(inputFile.exists()) {
            throw IllegalStateException("file $didFilePath not found")
        }

        val idlFileDeclaration = CandidFileParser.parseFile(inputFile.readText())

        /**
         * We need to pass [fileName] when a typeAlias needs to refer a
         * class that will be declared later in the file:
         *
         * ```
         * typealias Ledger = Array<LedgerCanister.Block>
         * object LedgerCanister {
         *     class Block (
         *         val parent_hash: Hash?,
         *         val transaction: Transaction,
         *         val timestamp: TimeStamp
         *     )
         * }
         * ```
         */
        val kotlinGeneratedClasses = idlFileDeclaration.types.map {
            IDLTypeDeclarationConverter(
                input = it.typeDefinition,
                className = fileName
            )
        }

        val classes = kotlinGeneratedClasses.filter {
            it.classDefinitionType is KotlinClassDefinitionType.Class
                    // || it.classDefinitionType ==KotlinClassDefinitionType.SealedClass
        }

        // TODO, typealias

        // Add file comment
        idlFileDeclaration.comment?.let {
            fileText.append(KotlinCommentGenerator.getKotlinComment(it))
        }

        fileText.appendLine("object $fileName{\n")

        // Additional classes declaration
        fileText.appendLine(
            classes.joinToString("\n") { it.kotlinDefinition(showCandidDefinition) }
        )

       /* val typeAliases = kotlinGeneratedClasses.filter {
            it.classDefinitionType is KotlinClassDefinitionType.TypeAlias
                    || it.classDefinitionType is KotlinClassDefinitionType.Array
                    || it.classDefinitionType is KotlinClassDefinitionType.Function
        }*/

        /*

        // TypeAliases must be declare before object definition
        writeTypeAliases(typeAliases)



        // Define service
        val kotlinServiceDefinition = idlFileDeclaration.service?.let {
            KotlinServiceGenerator(
                idlFileService = it,
                serviceName = fileName,
                showCandidDefinition = showCandidDefinition,
                removeCandidComment = removeCandidComment
            ).getKotlinServiceDefinition()
        }
        kotlinServiceDefinition?.let { fileText.appendLine(it) }

        */
        fileText.appendLine("}")
        outputFile.writeText(fileText.toString().toKotlinFileString())
    }

    companion object {
        private const val HEADER = """// TODO, add package name
        
        import java.math.BigInteger
        import com.bity.icp_kotlin_kit.candid.CandidDecoder
        import com.bity.icp_kotlin_kit.candid.CandidEncoder
        import com.bity.icp_kotlin_kit.domain.model.ICPMethod
        import com.bity.icp_kotlin_kit.candid.model.CandidValue
        import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
        import com.bity.icp_kotlin_kit.domain.request.PollingValues
        import com.bity.icp_kotlin_kit.provideICPCanisterRepository
        import com.bity.icp_kotlin_kit.domain.model.ICPSigningPrincipal
        import com.bity.icp_kotlin_kit.plugin.candid_parser.util.shared.*
        import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
        import com.bity.icp_kotlin_kit.domain.model.enum.ICPRequestCertification
       
        /**
         * File generated using ICP Kotlin Kit Plugin
         */
    """
    }
}