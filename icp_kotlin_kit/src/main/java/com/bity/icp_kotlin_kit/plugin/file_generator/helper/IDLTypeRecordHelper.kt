package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidRecordParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLRecordHelper.idlRecordToKotlinClassVariable

internal object IDLTypeRecordHelper {

    internal fun typeRecordToKotlinClass(
        className: String,
        type: IDLTypeRecord
    ): String {

        val kotlinClassString = StringBuilder().appendLine("class $className (")

        val idlRecordDeclaration = CandidRecordParser.parseRecord(type.recordDeclaration)
        val variablesDeclaration = idlRecordDeclaration.records
            .joinToString(",\n") { idlRecordToKotlinClassVariable(it) }
        kotlinClassString.appendLine(variablesDeclaration)

        kotlinClassString.append(")")
        return kotlinClassString.toString()
    }
}