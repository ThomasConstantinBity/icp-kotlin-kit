package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLServiceDeclaration(
    val initArgsDeclaration: String? = null,
    val services: List<IDLService> = emptyList()
) {
    companion object : ParserNodeDeclaration<IDLServiceDeclaration> by reflective()
}