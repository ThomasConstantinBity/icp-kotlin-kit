package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidFuncParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.IDLFunArg
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFuncDeclaration

internal object KotlinFunctionGenerator {

    operator fun invoke(
        funId: String,
        idlTypeFunc: IDLTypeFuncDeclaration
    ): String {
        val idlFun = CandidFuncParser.parseFunc(idlTypeFunc.funcDeclaration)
        val inputArgs = getInputParamsDeclaration(idlFun.inputParams)
        val outputArgs = getOutputParamsDeclaration(idlFun.outputParams)
        return "typealias $funId = ($inputArgs) -> $outputArgs"
    }

    operator fun invoke(idlFun: IDLFun): String {
        val inputArgs = getInputParamsDeclaration(idlFun.inputParams)
        val outputArgs = getOutputParamsDeclaration(idlFun.outputParams)
        return "($inputArgs) -> $outputArgs"
    }

    private fun getInputParamsDeclaration(inputParams: List<IDLFunArg>): String =
        inputParams
            .mapNotNull {
                val kotlinClass = KotlinClassGenerator.getCorrespondingKotlinClass(it.idlType)
                if(it.argId != null) "${it.argId}: $kotlinClass"
                else kotlinClass
            }
            .joinToString()

    private fun getOutputParamsDeclaration(outputParams: List<IDLFunArg>): String {
        return when(val size = outputParams.size) {
            0 -> "Unit"

            1 -> TODO() /*{
                val param = outputParams.first()
                val argClass = KotlinClassGenerator.getCorrespondingKotlinClass(param.idlType)
                if(param.argId != null) "${param.argId} : $argClass"
                else argClass!!
            }*/

            else -> {
                val argsDeclaration = outputParams.map {
                    val kotlinClass = KotlinClassGenerator.getCorrespondingKotlinClass(it.idlType)
                    if(it.argId != null) "${it.argId}: $kotlinClass"
                    else kotlinClass
                }.joinToString()
                "NTuple${size}($argsDeclaration)"
            }
        }
    }
}