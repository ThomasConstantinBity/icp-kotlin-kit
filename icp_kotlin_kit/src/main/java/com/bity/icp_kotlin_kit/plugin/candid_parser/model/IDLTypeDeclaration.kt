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
}
