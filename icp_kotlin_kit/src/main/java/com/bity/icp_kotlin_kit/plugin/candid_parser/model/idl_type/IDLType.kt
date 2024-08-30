package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.subtype

// TODO, remove = false and fix errors
internal sealed class IDLType(
    val isOptional: Boolean = false
) {
    companion object : ParserNodeDeclaration<IDLType> by subtype()
}