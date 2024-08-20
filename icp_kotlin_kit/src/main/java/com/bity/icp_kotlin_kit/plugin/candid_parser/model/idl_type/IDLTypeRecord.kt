package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLTypeRecord(
    val recordDeclaration: String
) : IDLType() {
    companion object : ParserNodeDeclaration<IDLTypeRecord> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IDLTypeRecord

        return recordDeclaration == other.recordDeclaration
    }

    override fun hashCode(): Int {
        return recordDeclaration.hashCode()
    }


}