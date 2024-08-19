package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeText(
    typeId: String,
) : IDLType(typeId) {
    companion object : ParserNodeDeclaration<IDLTypeText> by reflective()
}