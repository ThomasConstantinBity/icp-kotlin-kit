package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeFunc(
    typeId: String,
    val inputParams: List<String>,
    val outputParams: List<String>,
    val funcType: String? = null
) : IDLType(typeId) {
    companion object : ParserNodeDeclaration<IDLTypeFunc> by reflective()
}