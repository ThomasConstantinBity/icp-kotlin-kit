package com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_comment

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.subtype

internal sealed class IDLComment {
    companion object : ParserNodeDeclaration<IDLComment> by subtype()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}