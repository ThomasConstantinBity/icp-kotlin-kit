package com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_file

import com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_comment.IDLComment
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

// TODO remove
internal data class IDLFileType(
    val comment: IDLComment? = null,
    val typeDefinition: String
) {
    companion object : ParserNodeDeclaration<IDLFileType> by reflective()
}