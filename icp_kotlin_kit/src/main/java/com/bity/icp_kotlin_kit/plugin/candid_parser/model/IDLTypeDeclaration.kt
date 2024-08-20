package com.bity.icp_kotlin_kit.plugin.candid_parser.model

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.comment.IDLSingleLineComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.subtype
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLTypeDeclaration(
    val comment: IDLComment? = null,
    val isOptional: Boolean = false,
    val id: String,
    val type: IDLType,
) {
    companion object : ParserNodeDeclaration<IDLTypeDeclaration> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IDLTypeDeclaration

        if (comment != other.comment) return false
        if (isOptional != other.isOptional) return false
        if (id != other.id) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = comment?.hashCode() ?: 0
        result = 31 * result + isOptional.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
