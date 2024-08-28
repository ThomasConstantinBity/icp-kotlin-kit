package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidRecordParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLRecordHelper.idlRecordToKotlinClassVariable

internal object IDLTypeRecordHelper {

    internal fun typeRecordToKotlinClass(
        className: String,
        type: IDLTypeRecord
    ): String {
        // TODO, use an array
        val innerClasses = mutableListOf<String>()
        val kotlinClassString = StringBuilder().appendLine("class $className (")

        val idlRecordDeclaration = CandidRecordParser.parseRecord(type.recordDeclaration)
        val variablesDeclaration = idlRecordDeclaration.records
            .joinToString(",\n") {

                if(it.type is IDLTypeVec) {
                    val idlVec = CandidVecParser.parseVec(it.type.vecDeclaration)
                    when(val idlVecType = idlVec.type) {
                        // Need to declare a new class
                        is IDLTypeRecord -> {
                            val variableName = it.id
                            val arrayClassName = getClassNameFromVariableName(variableName)
                            innerClasses.add(typeRecordToKotlinClass(arrayClassName, idlVecType))
                            "val ${variableName}: ${IDLTypeVecHelper.kotlinDefinition(arrayClassName)}"
                        }
                        else -> idlRecordToKotlinClassVariable(it)
                    }
                } else idlRecordToKotlinClassVariable(it)
            }
        kotlinClassString.appendLine(variablesDeclaration)

        if(innerClasses.isNotEmpty()) {
            kotlinClassString.appendLine(") {")
            innerClasses.forEach {
                kotlinClassString.appendLine(it)
            }
            kotlinClassString.appendLine("}")
        } else kotlinClassString.append(")")
        return kotlinClassString.toString()
    }

    private fun getClassNameFromVariableName(variableName: String) =
        variableName.split("_")
            .joinToString("") { name ->
                name.replaceFirstChar { it.uppercaseChar() } }
}