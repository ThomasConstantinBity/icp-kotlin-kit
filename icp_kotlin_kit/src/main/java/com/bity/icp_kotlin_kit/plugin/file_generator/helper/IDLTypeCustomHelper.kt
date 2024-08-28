package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import java.util.Locale

internal object IDLTypeCustomHelper {

    fun idlTypeCustomToKotlinClass(
        className: String,
        idlTypeCustom: IDLTypeCustom
    ): String {
        val classDefinition = StringBuilder().appendLine("class $className(")
        val variableName = idlTypeCustom.typeDef
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }
        classDefinition.appendLine("val $variableName: ${idlTypeCustom.typeDef}")
        classDefinition.append(")")
        return classDefinition.toString()
    }
}