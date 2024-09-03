package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeVec(
    val vecDeclaration: String,
    isOptional: Boolean = false,
    id: String? = null
) : IDLType(
    isOptional = isOptional,
    id = id
) {
    companion object : ParserNodeDeclaration<IDLTypeVec> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLTypeVec

        return vecDeclaration == other.vecDeclaration
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + vecDeclaration.hashCode()
        return result
    }
}