package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeCustom(
    typeId: String,
    val typeDef: String
) : IDLType(typeId) {
    companion object : ParserNodeDeclaration<IDLTypeCustom> by reflective()
}