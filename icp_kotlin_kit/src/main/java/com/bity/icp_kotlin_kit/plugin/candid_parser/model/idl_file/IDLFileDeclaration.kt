package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

// TODO, add support for service constructor
internal data class IDLFileDeclaration(
    val comment: IDLComment? = null,
    val types: List<IDLFileType> = emptyList(),
    val services: List<IDLService> = emptyList()
) {
    companion object : ParserNodeDeclaration<IDLFileDeclaration> by reflective()
}