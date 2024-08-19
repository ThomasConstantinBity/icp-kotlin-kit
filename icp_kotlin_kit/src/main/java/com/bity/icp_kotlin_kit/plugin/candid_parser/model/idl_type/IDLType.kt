package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.subtype

internal sealed class IDLType(
    val typeId: String?,
    val isOptional: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IDLType

        if (typeId != other.typeId) return false
        if (isOptional != other.isOptional) return false

        return true
    }

    override fun hashCode(): Int {
        var result = typeId?.hashCode() ?: 0
        result = 31 * result + isOptional.hashCode()
        return result
    }

    companion object : ParserNodeDeclaration<IDLType> by subtype()

}