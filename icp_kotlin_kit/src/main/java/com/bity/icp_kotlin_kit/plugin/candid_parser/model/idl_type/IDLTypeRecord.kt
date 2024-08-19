package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeRecord(
    typeId: String,
    val records: List<IDLType>
) : IDLType(typeId) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLTypeRecord

        return records == other.records
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + records.hashCode()
        return result
    }

    companion object : ParserNodeDeclaration<IDLTypeRecord> by reflective()
}