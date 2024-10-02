package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLRecord

internal object IDLTypeRecordHelper {

    fun kotlinClassDefinition(
        idlRecord: IDLRecord,
        className: String
    ): KotlinClassDefinition {
        val kotlinClass = KotlinClassDefinition.Class(
            className = className
        )
        val params = idlRecord.types.map {
            var clazzName: String? = null
            val innerClass = IDLTypeHelper.getInnerTypeToDeclare(it)
            innerClass?.let { classToDeclare ->
                clazzName = UnnamedClassHelper.getUnnamedClassName()
                val clazz = kotlinClassDefinition(
                    idlRecord = classToDeclare,
                    className = clazzName!!
                )
                kotlinClass.innerClasses.add(clazz)
            }
            it.getKotlinClassParameter(clazzName)
        }
        kotlinClass.params.addAll(params)
        return kotlinClass
    }

}