package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService

internal object IDLServiceHelper {

    fun convertServiceIntoKotlinFunction(idlService: IDLService): String {
        val functionDeclaration = StringBuilder("suspend fun ${idlService.id}(")

        // Input args

        functionDeclaration.append("): ")

        // Output args
        functionDeclaration.appendLine("Unit {")

        functionDeclaration.append("}")
        return functionDeclaration.toString()
    }
}