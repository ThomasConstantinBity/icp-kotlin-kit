package com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_comment

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLSingleLineComment(
    val commentLines: List<String>,
): IDLComment() {
    companion object : ParserNodeDeclaration<IDLSingleLineComment> by reflective()
}