package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidFileParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.enum.KotlinClassDefinitionType
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
        val kotlinGeneratedClasses = idlFileDeclaration.types.map {
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
            IDLTypeDeclarationConverter(
                input = it.typeDefinition,
                className = fileName
            )
        }
        val typeAliases = kotlinGeneratedClasses.filter {
            it.classDefinitionType == KotlinClassDefinitionType.TypeAlias
                    || it.classDefinitionType == KotlinClassDefinitionType.Array
                    || it.classDefinitionType == KotlinClassDefinitionType.Function
        }
        val classes = kotlinGeneratedClasses.filter {
            it.classDefinitionType == KotlinClassDefinitionType.Class
                    || it.classDefinitionType ==KotlinClassDefinitionType.SealedClass
        }

        // TypeAliases must be declare before object definition
        fileText.appendLine(typeAliases.joinToString("\n") {
            if(showCandidDefinition)
                """
                    /**
                    ${CandidDefinitionHelper.candidDefinition(
                        definition = it.candidDefinition,
                        removeCandidComment = removeCandidComment
                    )}
                    */
                    ${it.kotlinDefinition}
                """.trimIndent()
            else it.kotlinDefinition
        })
        // Add file comment
        idlFileDeclaration.comment?.let {
            fileText.append(KotlinCommentGenerator.getKotlinComment(it))
        }
        fileText.appendLine("object $fileName{\n")
        fileText.appendLine(classes.joinToString("\n") { it.kotlinDefinition })
        fileText.appendLine("}")
        outputFile.writeText(fileText.toString().toKotlinFileString())

        /*val typeAliasesAndClasses = KotlinClassGenerator.getTypeAliasAndClassDefinitions(
            types = idlFileDeclaration.types,
            fileName = fileName,
            showCandidDefinition = showCandidDefinition,
            removeCandidComment = removeCandidComment
        )

        // TypeAliases mus be declared outside object
        fileText.appendLine(typeAliasesAndClasses.first.joinToString(""))

        // IDL file comment
        idlFileDeclaration.comment?.let {
            fileText.appendLine()
            fileText.append(KotlinCommentGenerator.getKotlinComment(it))
        }

        fileText.appendLine(
            """object $fileName {
                    ${typeAliasesAndClasses.second.joinToString("")}
                    ${idlFileDeclaration.service?.let {
                        KotlinServiceGenerator(
                            idlFileService = it,
                            serviceName = fileName,
                            showCandidDefinition = showCandidDefinition,
                            removeCandidComment = removeCandidComment
                        ).getServiceText()
                    } ?: ""}
                }
            """
        )

        return fileText.toString().toKotlinFileString()*/
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