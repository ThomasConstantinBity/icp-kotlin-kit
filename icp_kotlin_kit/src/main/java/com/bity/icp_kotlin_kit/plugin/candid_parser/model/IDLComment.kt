package com.bity.icp_kotlin_kit.plugin.candid_parser.model

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLSingleLineComment(
    val commentLines: List<String>,
) {
    companion object : ParserNodeDeclaration<IDLSingleLineComment> by reflective()
}