package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_vec

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLVec(
    val isOptional: Boolean = false,
    val type: IDLType
) {
    companion object : ParserNodeDeclaration<IDLVec> by reflective()
}