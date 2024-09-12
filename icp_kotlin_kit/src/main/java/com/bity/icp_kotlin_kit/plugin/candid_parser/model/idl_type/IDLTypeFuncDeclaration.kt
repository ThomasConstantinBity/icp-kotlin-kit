package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal class IDLTypeFuncDeclaration(
    comment: IDLComment? = null,
    id: String? = null,
    isOptional: Boolean = false,
    val funcDeclaration: String,
) : IDLType(
    comment = comment,
    id = id,
    isOptional = isOptional
) {
    companion object : ParserNodeDeclaration<IDLTypeFuncDeclaration> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLTypeFuncDeclaration

        return funcDeclaration == other.funcDeclaration
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + funcDeclaration.hashCode()
        return result
    }
}