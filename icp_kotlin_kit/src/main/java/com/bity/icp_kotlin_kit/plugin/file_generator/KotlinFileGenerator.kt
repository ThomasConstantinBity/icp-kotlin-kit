package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.toKotlinFileString

internal object KotlinFileGenerator {

    private const val HEADER = """
        // TODO, add package name
        
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

    fun generateFileText(
        idlFileDeclaration: IDLFileDeclaration,
        fileName: String,
        showCandidDefinition: Boolean = true,
        removeCandidComment: Boolean = false
    ): String {

        val fileText = StringBuilder(HEADER).appendLine()

        val typeAliasesAndClasses = KotlinClassGenerator.getTypeAliasAndClassDefinitions(
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
                        KotlinServiceGenerator.getServiceText(
                            idlFileService = it,
                            serviceName = fileName,
                            showCandidDefinition = removeCandidComment,
                            removeCandidComment = removeCandidComment
                        )
                    } ?: ""}
                }
            """
        )

        return fileText.toString().toKotlinFileString()
    }
}