package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_vec

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLVec(
    val id: String? = null,
    val isOptional: Boolean = false,
    val type: IDLType
) {
    companion object : ParserNodeDeclaration<IDLVec> by reflective()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IDLVec

        if (id != other.id) return false
        if (isOptional != other.isOptional) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + isOptional.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }


}