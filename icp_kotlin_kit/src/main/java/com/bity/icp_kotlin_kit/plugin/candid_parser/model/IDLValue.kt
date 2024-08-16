package com.bity.icp_kotlin_kit.plugin.candid_parser.model

import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.subtype
import guru.zoroark.tegral.niwen.parser.reflective

internal sealed class IDLValue {
    companion object : ParserNodeDeclaration<IDLValue> by subtype()
}

internal data class IDLBoolean(val value: Boolean) : IDLValue() {
    companion object : ParserNodeDeclaration<IDLBoolean> by reflective()
}

internal data class IDLDecimal(val decimal: String, val sign: String) : IDLValue() {
    companion object : ParserNodeDeclaration<IDLDecimal> by reflective()
}

internal data class IDLText(val text: String) : IDLValue() {
    companion object : ParserNodeDeclaration<IDLText> by reflective()
}

internal data class IDLHex(val hexValue: String) : IDLValue() {
    companion object : ParserNodeDeclaration<IDLHex> by reflective()
}

internal class IDLNull : IDLValue() {
    companion object : ParserNodeDeclaration<IDLNull> by reflective()
}