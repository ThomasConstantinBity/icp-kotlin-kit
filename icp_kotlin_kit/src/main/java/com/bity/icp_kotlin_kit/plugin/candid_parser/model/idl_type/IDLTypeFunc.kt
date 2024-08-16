package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLTypeFunc(
    val inputParams: List<String>,
    val outputParams: List<String>,
    val funcType: String? = null
) : IDLType() {
    companion object : ParserNodeDeclaration<IDLTypeFunc> by reflective()
}