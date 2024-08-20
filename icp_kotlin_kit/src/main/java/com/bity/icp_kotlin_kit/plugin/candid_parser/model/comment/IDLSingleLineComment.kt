package com.bity.icp_kotlin_kit.plugin.candid_parser.model.comment

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLSingleLineComment(
    val commentLines: List<String>,
): IDLComment() {
    companion object : ParserNodeDeclaration<IDLSingleLineComment> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IDLSingleLineComment

        return commentLines == other.commentLines
    }

    override fun hashCode(): Int {
        return commentLines.hashCode()
    }
}