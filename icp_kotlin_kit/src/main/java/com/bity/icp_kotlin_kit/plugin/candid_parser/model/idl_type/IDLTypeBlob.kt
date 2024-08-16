package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeBlob : IDLType() {

    override fun equals(other: Any?): Boolean {
        return other is IDLTypeBlob
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    companion object : ParserNodeDeclaration<IDLTypeBlob> by reflective()
}