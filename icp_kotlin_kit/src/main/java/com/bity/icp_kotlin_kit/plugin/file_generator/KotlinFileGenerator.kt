package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.toKotlinFileString
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper
import java.io.File
import java.lang.IllegalStateException
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinClassFinder.Result.KotlinClass

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
        writeService()
        fileText.appendLine("}")
        outputFile.writeText(fileText.toString().toKotlinFileString())
    }

    private fun writeTypeAliases() {
        idlFileDeclaration.types
            .filter {
                it is IDLTypeCustom
                        || it is IDLTypeVec
            }
            .map { type ->
                when(type) {
                    is IDLTypeCustom -> {
                        requireNotNull(type.typeDef)
                        requireNotNull(type.type)
                        KotlinClassDefinition.TypeAlias(
                            typeAliasId = type.typeDef,
                            type = type.type,
                            typeClassName = fileName
                        )
                    }
                    is IDLTypeVec -> {
                        requireNotNull(type.vecDeclaration)
                        KotlinClassDefinition.TypeAlias(
                            typeAliasId = type.vecDeclaration,
                            type = type,
                            typeClassName = fileName
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

    private fun writeService() {
        val serviceFunctions = idlFileDeclaration.services
            .map {
                requireNotNull(it.id)
                KotlinClassDefinition.ICPQuery(
                    comment = it.comment,
                    queryName = it.id,
                    inputArgs = it.inputArgs.map { arg -> arg.getKotlinClassParameter() },
                    outputArgs = it.outputArgs.map { arg -> arg.getKotlinClassParameter() }
                )
            }
        fileText.appendLine(
            """
                class ${fileName}Service(
                    private val canister: ICPPrincipal
                ) {
                    ${serviceFunctions.joinToString(
                        prefix = "\n",
                        separator = "\n\n"
                    ) { it.kotlinDefinition() }}
                }
            """.trimIndent()
        )
    }

    companion object {
        private const val HEADER = """// TODO, add package name
        
        import java.math.BigInteger
        import com.bity.icp_kotlin_kit.candid.CandidDecoder
        import com.bity.icp_kotlin_kit.domain.usecase.ICPQuery
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