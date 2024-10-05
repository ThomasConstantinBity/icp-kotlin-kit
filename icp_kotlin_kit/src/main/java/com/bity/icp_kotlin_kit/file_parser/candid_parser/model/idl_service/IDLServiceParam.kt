package com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_service

import com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_type.IDLType
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLServiceParam(
    val params: List<IDLType> = emptyList()
) {
    companion object : ParserNodeDeclaration<IDLServiceParam> by reflective()
}