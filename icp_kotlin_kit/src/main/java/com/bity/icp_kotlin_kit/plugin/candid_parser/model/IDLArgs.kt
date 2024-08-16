package com.bity.icp_kotlin_kit.plugin.candid_parser.model

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

internal data class IDLArgsWrapper(val idlArgs: IDLArgs) {
    companion object : ParserNodeDeclaration<IDLArgsWrapper> by reflective()
}

internal data class IDLArgs(val args: List<IDLValue>) {
    companion object : ParserNodeDeclaration<IDLArgs> by reflective()
}