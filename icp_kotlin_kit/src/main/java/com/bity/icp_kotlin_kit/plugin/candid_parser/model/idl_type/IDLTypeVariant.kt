package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeVariant(
    typeId: String,
    val types: List<IDLType>
) : IDLType(typeId) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLTypeVariant

        return types == other.types
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + types.hashCode()
        return result
    }

    companion object : ParserNodeDeclaration<IDLTypeVariant> by reflective()
}