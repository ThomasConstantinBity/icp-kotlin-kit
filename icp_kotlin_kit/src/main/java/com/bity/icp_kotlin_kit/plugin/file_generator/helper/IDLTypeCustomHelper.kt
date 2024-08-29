package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName

internal object IDLTypeCustomHelper {

    fun idlTypeCustomToKotlinClass(
        className: String,
        idlTypeCustom: IDLTypeCustom
    ): String {
        val classDefinition = StringBuilder().appendLine("class $className(")
        val variableName = idlTypeCustom.typeDef.kotlinVariableName()
        classDefinition.appendLine("val $variableName: ${idlTypeCustom.typeDef}")
        classDefinition.append(")")
        return classDefinition.toString()
    }
}