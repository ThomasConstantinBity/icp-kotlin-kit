package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLFunArg(
    val argId: String? = null,
    val idlType: IDLType
) {
    companion object : ParserNodeDeclaration<IDLFunArg> by reflective()
}