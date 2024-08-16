package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.subtype

internal sealed class IDLType(
    val typeId: String
) {

    override fun equals(other: Any?): Boolean {
        return (other as? IDLType)?.let {
            it.typeId == typeId
        } ?: false
    }

    override fun hashCode(): Int {
        return typeId.hashCode()
    }

    companion object : ParserNodeDeclaration<IDLType> by subtype()
}