package com.bity.icp_kotlin_kit.plugin.candid_parser.model

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.subtype
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLTypeDeclaration(
    val typeId: String,
    val idlTypeList: IDLTypeList,
) {
    companion object : ParserNodeDeclaration<IDLTypeDeclaration> by reflective()
}

internal data class IDLTypeList(val types: List<IDLType>) {
    companion object : ParserNodeDeclaration<IDLTypeList> by reflective()
}
