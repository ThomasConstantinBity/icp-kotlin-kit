package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeFunc(
    typeId: String,
    val inputParams: List<String>,
    val outputParams: List<String>,
    val funcType: String? = null
) : IDLType(typeId) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLTypeFunc

        if (inputParams != other.inputParams) return false
        if (outputParams != other.outputParams) return false
        if (funcType != other.funcType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + inputParams.hashCode()
        result = 31 * result + outputParams.hashCode()
        result = 31 * result + (funcType?.hashCode() ?: 0)
        return result
    }

    companion object : ParserNodeDeclaration<IDLTypeFunc> by reflective()
}