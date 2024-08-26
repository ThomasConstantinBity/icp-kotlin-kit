package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeFuncDeclaration(
    val funcDeclaration: String
) : IDLType() {
    companion object : ParserNodeDeclaration<IDLTypeFuncDeclaration> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IDLTypeFuncDeclaration

        return funcDeclaration == other.funcDeclaration
    }

    override fun hashCode(): Int {
        return funcDeclaration.hashCode()
    }
}