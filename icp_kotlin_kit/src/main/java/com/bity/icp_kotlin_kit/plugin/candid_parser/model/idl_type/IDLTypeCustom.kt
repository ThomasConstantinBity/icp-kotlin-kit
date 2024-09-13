package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassParameter
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLTypeCustom(
    override val comment: IDLComment? = null,
    override val isOptional: Boolean = false,
    override val id: String? = null,
    val typeDef: String? = null,
    val type: IDLType? = null
) : IDLType(
    comment = comment,
    id = id,
    isOptional = isOptional
) {

    companion object : ParserNodeDeclaration<IDLTypeCustom> by reflective()

    override fun typeVariable(): String {
        requireNotNull(typeDef)
        return typeDef
    }

    override fun getKotlinClassParameter(): KotlinClassParameter {
        val typeVariable = type?.typeVariable() ?: typeDef
        requireNotNull(id)
        requireNotNull(typeVariable)
        return KotlinClassParameter(
            comment = comment,
            id = id,
            isOptional = isOptional,
            typeVariable = typeVariable
        )
    }

    override fun getKotlinClassDefinition(): KotlinClassDefinition {
        requireNotNull(typeDef)
        val kotlinClass = KotlinClassDefinition.Class(
            className = typeDef
        )
        kotlinClass.params.add(
            KotlinClassParameter(
                comment = comment,
                id = typeDef.kotlinVariableName(),
                isOptional = isOptional,
                typeVariable = typeDef
            )
        )
        return kotlinClass
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLTypeCustom

        if (typeDef != other.typeDef) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + typeDef.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}