package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeFunc(
    val funcDeclaration: String
) : IDLType() {
    companion object : ParserNodeDeclaration<IDLTypeFunc> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IDLTypeFunc

        return funcDeclaration == other.funcDeclaration
    }

    override fun hashCode(): Int {
        return funcDeclaration.hashCode()
    }


}