package com.bity.icp_kotlin_kit.plugin.candid_parser.model

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class CandidFile(val args: IDLArgs) {
    companion object : ParserNodeDeclaration<CandidFile> by reflective()
}