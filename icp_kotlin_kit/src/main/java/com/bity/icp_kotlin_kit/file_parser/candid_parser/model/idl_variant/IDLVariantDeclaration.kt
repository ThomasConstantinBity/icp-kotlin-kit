package com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_variant

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

// TODO, could be removed
internal data class IDLVariantDeclaration(
    val variants: List<IDLVariant>
) {
    companion object : ParserNodeDeclaration<IDLVariantDeclaration> by reflective()
}