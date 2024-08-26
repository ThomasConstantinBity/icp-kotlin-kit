package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_func

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

// TODO, params can be null
// TODO fun can be null
internal data class IDLFun(
    val inputParams: List<IDLType> = listOf(),
    val outputParams: List<IDLType> = listOf(),
    val funType: FunType? = null
) {
    companion object : ParserNodeDeclaration<IDLFun> by reflective()
}