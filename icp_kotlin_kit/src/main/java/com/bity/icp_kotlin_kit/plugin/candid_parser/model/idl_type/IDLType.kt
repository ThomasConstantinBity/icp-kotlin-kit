package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.subtype

internal sealed class IDLType(
    val isOptional: Boolean
) {
    companion object : ParserNodeDeclaration<IDLType> by subtype()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IDLType

        return isOptional == other.isOptional
    }

    override fun hashCode(): Int {
        return isOptional.hashCode()
    }
}