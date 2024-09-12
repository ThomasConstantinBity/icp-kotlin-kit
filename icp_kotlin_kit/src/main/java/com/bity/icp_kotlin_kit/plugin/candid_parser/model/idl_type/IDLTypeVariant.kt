package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLTypeVariant(
    val variantDeclaration: String
) : IDLType(
    isOptional = false
) {
    companion object : ParserNodeDeclaration<IDLTypeVariant> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IDLTypeVariant

        return variantDeclaration == other.variantDeclaration
    }

    override fun hashCode(): Int {
        return variantDeclaration.hashCode()
    }
}