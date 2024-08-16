package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeRecord(
    typeId: String,
    val records: List<IDLType>
) : IDLType(typeId) {
    companion object : ParserNodeDeclaration<IDLTypeRecord> by reflective()
}