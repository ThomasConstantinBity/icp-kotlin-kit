package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.toKotlinFileString
import java.io.File
import java.lang.IllegalStateException

internal class KotlinFileGenerator(
    private val didFilePath: String,
    outputFilePath: String,
) {

    private val idlFileDeclaration: IDLFileDeclaration
    private val fileName = didFilePath.split("/")
        .last()
        .removeSuffix(".did")
    private val outputFile = File(outputFilePath)

    private val fileText = StringBuilder(HEADER)

    init {
        val inputFile = File(didFilePath)
        assert(inputFile.exists()) {
            throw IllegalStateException("file $didFilePath not found")
        }

        idlFileDeclaration = CandidParser.parseFile(inputFile.readText())
    }

    fun generateKotlinFile() {

        // TypeAliases must be declared before object declaration
        writeTypeAliases()
        fileText.appendLine("\n")

        fileText.appendLine("object $fileName {")
        writeClasses()
        fileText.appendLine("}")
        outputFile.writeText(fileText.toString().toKotlinFileString())
        /*val typeDeclarationConverter = IDLTypeDeclarationConverter(
            fileName = fileName,
            types = TODO()// idlFileDeclaration.types
        )
        val kotlinGeneratedClasses = typeDeclarationConverter.convertTypes()

        val typeAliases = kotlinGeneratedClasses.filter {
            it.classDefinitionType is KotlinClassDefinition.TypeAlias
        }

        val classes = kotlinGeneratedClasses.filter {
            it.classDefinitionType is KotlinClassDefinition.Class
                    || it.classDefinitionType is KotlinClassDefinition.SealedClass
                    || it.classDefinitionType is KotlinClassDefinition.Function
        }

        // TypeAliases must be declare before object definition
        typeAliases.forEach {
            fileText.appendLine(it.kotlinDefinition(showCandidDefinition))
        }

        // Add file comment
        idlFileDeclaration.comment?.let {
            fileText.append(KotlinCommentGenerator.getKotlinComment(it))
        }

        fileText.appendLine("object $fileName {\n")

        // Additional classes declaration
        fileText.appendLine(
            classes.joinToString("\n") {
                it.kotlinDefinition(showCandidDefinition)
            }
        )*/

        // Service declaration
        /*val idlFileServiceConverter = IDLFileServiceConverter(
            fileName = fileName,
            services = idlFileDeclaration.services,
        )
        fileText.appendLine(
            idlFileServiceConverter.getKotlinServiceDefinition().kotlinDefinition()
        )*/
    }

    private fun writeTypeAliases() {
        idlFileDeclaration.types
            .filter { it is IDLTypeCustom || it is IDLTypeVec }
            .map { type ->
                when(type) {
                    is IDLTypeCustom -> {
                        val typeAliasId = type.typeDef
                            ?: throw IllegalStateException("typeAliasId required for $type")
                        val typeAliasType = type.type
                            ?: throw IllegalStateException("type required for $type")
                        KotlinClassDefinition.TypeAlias(
                            typeAliasId = typeAliasId,
                            type = typeAliasType
                        )
                    }
                    is IDLTypeVec -> {
                        val typeAliasId = type.vecDeclaration
                            ?: throw IllegalStateException("typeAliasId required for $type")
                        KotlinClassDefinition.TypeAlias(
                            typeAliasId = typeAliasId,
                            type = type
                        )
                    }
                    else -> throw Error("$type can't be a typealias")
                }
            }
            .forEach { fileText.appendLine(it.kotlinDefinition()) }
    }

    private fun writeClasses() {
        idlFileDeclaration.types
            .filter { it !is IDLTypeCustom && it !is IDLTypeVec }
            .map { it.getKotlinClassDefinition() }
            .forEach { fileText.appendLine(it.kotlinDefinition()) }
    }

    companion object {
        private const val HEADER = """// TODO, add package name
        
        import java.math.BigInteger
        import com.bity.icp_kotlin_kit.candid.CandidDecoder
        import com.bity.icp_kotlin_kit.candid.CandidEncoder
        import com.bity.icp_kotlin_kit.domain.model.ICPMethod
        import com.bity.icp_kotlin_kit.domain.usecase.ICPQuery
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