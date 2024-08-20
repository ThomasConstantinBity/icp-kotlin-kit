package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.subtype

internal sealed class IDLType {
    companion object : ParserNodeDeclaration<IDLType> by subtype()
}