package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassParameter
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_record.IDLRecord
import com.bity.icp_kotlin_kit.plugin.file_generator.KotlinCommentGenerator
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper.kotlinTypeVariable

internal object IDLRecordHelper {

    fun idlRecordToKotlinClassVariable(
        idlRecord: IDLRecord
    ): String {
        val kotlinClassVariable = StringBuilder()

        // Add comment
        idlRecord.comment?.let {
            kotlinClassVariable.append(KotlinCommentGenerator.getKotlinComment(it))
        }

        val variableName = idlRecord.id ?: kotlinTypeVariable(idlRecord.type).lowercase()
        val variableDefinition = StringBuilder("val $variableName: ")
        variableDefinition.append(kotlinTypeVariable(idlRecord.type))
        if(idlRecord.isOptional) variableDefinition.append("?")
        kotlinClassVariable.append(variableDefinition)
        return kotlinClassVariable.toString()
    }
}