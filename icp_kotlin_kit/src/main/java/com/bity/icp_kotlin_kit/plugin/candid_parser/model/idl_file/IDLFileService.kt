package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLFileService(
    val comment: IDLComment? = null,
    val serviceDefinition: String
) {
    companion object : ParserNodeDeclaration<IDLFileService> by reflective()
}