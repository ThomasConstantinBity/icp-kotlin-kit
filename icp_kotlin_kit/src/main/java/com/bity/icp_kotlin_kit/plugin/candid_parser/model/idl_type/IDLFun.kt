package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.FunType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.IDLFunArg
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

// TODO, params can be null
// TODO fun can be null
internal class IDLFun(
    val inputParams: List<IDLFunArg> = listOf(),
    val outputParams: List<IDLFunArg> = listOf(),
    val funType: FunType? = null,
) : IDLType(false) {
    companion object : ParserNodeDeclaration<IDLFun> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLFun

        if (inputParams != other.inputParams) return false
        if (outputParams != other.outputParams) return false
        if (funType != other.funType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + inputParams.hashCode()
        result = 31 * result + outputParams.hashCode()
        result = 31 * result + (funType?.hashCode() ?: 0)
        return result
    }
}