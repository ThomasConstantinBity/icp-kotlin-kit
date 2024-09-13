package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLTypeNull(
    override val comment: IDLComment? = null,
    override val isOptional: Boolean = false,
    override val id: String? = null,
    val nullDefinition: String? = null
) : IDLType(
    comment = comment,
    id = id,
    isOptional = isOptional
) {
    companion object : ParserNodeDeclaration<IDLTypeNull> by reflective()

    override fun getKotlinClassDefinition(): KotlinClassDefinition {
        requireNotNull(nullDefinition)
        return KotlinClassDefinition.Object(
            objectName = nullDefinition
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLTypeNull

        return nullDefinition == other.nullDefinition
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (nullDefinition?.hashCode() ?: 0)
        return result
    }
}