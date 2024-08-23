package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLFileDeclaration(
    val comment: IDLComment? = null,
    val types: List<IDLFileType> = emptyList(),
    val service: IDLFileService? = null
) {
    companion object : ParserNodeDeclaration<IDLFileDeclaration> by reflective()
}