package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_record

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

// TODO, could be removed
internal data class IDLRecordDeclaration(
    val records: List<IDLRecord>
) {
    companion object : ParserNodeDeclaration<IDLRecordDeclaration> by reflective()
}