package com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_service

import com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_comment.IDLComment
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLService(
    val comment: IDLComment? = null,
    val id: String,
    val inputParamsDeclaration: String,
    val outputParamsDeclaration: String,
    val serviceType: IDLServiceType? = null
) {
    companion object : ParserNodeDeclaration<IDLService> by reflective()
}