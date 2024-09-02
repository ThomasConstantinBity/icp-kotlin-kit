package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidFuncParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.IDLFunArg
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFuncDeclaration
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

internal object KotlinFunctionGenerator {

    operator fun invoke(
        className: String?,
        funId: String,
        idlTypeFunc: IDLTypeFuncDeclaration
    ): String {
        val idlFun = CandidFuncParser.parseFunc(idlTypeFunc.funcDeclaration)
        val inputArgs = getInputParamsDeclaration(idlFun.inputParams, className)
        val outputArgs = getOutputParamsDeclaration(idlFun.outputParams, className)
        return "typealias $funId = ($inputArgs) -> $outputArgs"
    }

    private fun getInputParamsDeclaration(
        inputParams: List<IDLFunArg>,
        className: String?
    ): String =
        inputParams.joinToString {
            val kotlinClass = IDLTypeHelper.kotlinTypeVariable(it.idlType, className)
            if (it.argId != null) "${it.argId}: $kotlinClass"
            else kotlinClass
        }

    private fun getOutputParamsDeclaration(
        outputParams: List<IDLFunArg>,
        className: String?
    ): String {
        return when(val size = outputParams.size) {

            0 -> "Unit"

            1 -> {
                val param = outputParams.first()
                val argClass = IDLTypeHelper.kotlinTypeVariable(param.idlType, className)
                if(param.argId != null) "${param.argId} : $argClass"
                else argClass
            }

            else -> {
                TODO()
                /*val argsDeclaration = outputParams.map {
                    val kotlinClass = IDLTypeDeclarationConverter.getCorrespondingKotlinClass(it.idlType)
                    if(it.argId != null) "${it.argId}: $kotlinClass"
                    else kotlinClass
                }.joinToString()
                "NTuple${size}($argsDeclaration)"*/
            }
        }
    }
}