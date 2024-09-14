package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassParameter
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeRecordHelper
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLTypeVec(
    override val comment: IDLComment? = null,
    override val isOptional: Boolean = false,
    override val id: String? = null,
    val vecDeclaration: String? = null,
    val vecType: IDLType
) : IDLType(
    comment = comment,
    id = id,
    isOptional = isOptional
) {
    companion object : ParserNodeDeclaration<IDLTypeVec> by reflective()

    override fun typeVariable(className: String?): String =
        "kotlin.Array<${vecType.typeVariable(className)}>"

    override fun getKotlinClassDefinition(): KotlinClassDefinition {
        requireNotNull(id)
        val kotlinClass = KotlinClassDefinition.Class(
            className = id
        )
        // If vecType is TypeRecord we need to declare an inner class
        val innerTypeToDeclare = IDLTypeHelper.getInnerTypeToDeclare(vecType)
        if(innerTypeToDeclare != null) {
            val className = "_ArrayClass"
            kotlinClass.innerClasses.add(
                IDLTypeRecordHelper.kotlinClassDefinition(
                    idlRecord = innerTypeToDeclare,
                    className = className
                )
            )
            kotlinClass.params.add(
                KotlinClassParameter(
                    comment = comment,
                    id = "values",
                    isOptional = isOptional,
                    typeVariable = "kotlin.Array<$className>"
                )
            )
        } else {
            kotlinClass.params.add(
                KotlinClassParameter(
                    comment = comment,
                    id = "values",
                    isOptional = isOptional,
                    typeVariable = typeVariable()
                )
            )
        }
        return kotlinClass
    }

    override fun getKotlinClassParameter(className: String?): KotlinClassParameter {
        requireNotNull(id)
        return KotlinClassParameter(
            comment = comment,
            id = id,
            isOptional = isOptional,
            typeVariable = typeVariable(className)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLTypeVec

        return vecType == other.vecType
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + vecType.hashCode()
        return result
    }


}