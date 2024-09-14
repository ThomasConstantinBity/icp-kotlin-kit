package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_fun.FunType
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLFun(
    override val comment: IDLComment? = null,
    override val isOptional: Boolean = false,
    override val id: String? = null,
    val funcName: String? = null,
    val inputArgs: List<IDLType> = emptyList(),
    val outputArgs: List<IDLType> = emptyList(),
    val funType: FunType? = null
) : IDLType(
    comment = comment,
    id = id,
    isOptional = isOptional
) {
    companion object : ParserNodeDeclaration<IDLFun> by reflective()

    override fun getKotlinClassDefinition(): KotlinClassDefinition {
        requireNotNull(funcName)
        return KotlinClassDefinition.Function(
            functionName = funcName,
            outputArgs = outputArgs.map { it.getKotlinClassDefinition() }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLFun

        if (funcName != other.funcName) return false
        if (inputArgs != other.inputArgs) return false
        if (outputArgs != other.outputArgs) return false
        if (funType != other.funType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (funcName?.hashCode() ?: 0)
        result = 31 * result + inputArgs.hashCode()
        result = 31 * result + outputArgs.hashCode()
        result = 31 * result + (funType?.hashCode() ?: 0)
        return result
    }


}