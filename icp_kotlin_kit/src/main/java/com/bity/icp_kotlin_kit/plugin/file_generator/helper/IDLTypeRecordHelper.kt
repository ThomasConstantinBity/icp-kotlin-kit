package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLRecord

internal object IDLTypeRecordHelper {

    fun kotlinClassDefinition(
        idlRecord: IDLRecord,
        className: String
    ): KotlinClassDefinition {
        return KotlinClassDefinition.Class(
            className = className
        ).apply {
            params.addAll(idlRecord.types.map { it.getKotlinClassParameter() })
        }
    }

}