package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec

internal object IDLTypeHelper {

    fun getInnerTypeToDeclare(idlType: IDLType): IDLRecord? {
        return when(idlType) {
            is IDLRecord -> return idlType
            is IDLTypeVec -> return getInnerTypeToDeclare(idlType.vecType)
            else -> null
        }
    }

    // TODO, can remove className ?
    fun kotlinTypeVariable(
        type: IDLType,
        className: String? = null
    ): String =
        when (type) {
            is IDLFun -> TODO()
            is IDLTypeBlob -> "ByteArray"
            is IDLTypeBoolean -> "Boolean"
            is IDLTypeCustom -> {
                type.typeDef
                    ?: className
                    ?: throw RuntimeException("Unable to define kotlin type variable for $type")
            }

            is IDLTypeInt -> {
                if(className != "Int") "Int" else "kotlin.Int"
            }
            is IDLTypeNat -> "UInt"
            is IDLTypeNat64 -> "ULong"
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> "ICPPrincipal"
            is IDLRecord -> className
                ?: throw RuntimeException("className is required for record declaration '${type}'")

            is IDLTypeText -> "String"
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> "Array<${kotlinTypeVariable(type.vecType)}${if(type.isOptional) "?" else ""}>"
        }

    fun kotlinGenericVariableName(idlType: IDLType) =
        when(idlType) {
            is IDLFun -> TODO()
            is IDLTypeBlob -> "byteArray"
            is IDLTypeBoolean -> "boolean"
            is IDLTypeCustom -> TODO()// idlType.typeDef.kotlinVariableName()
            is IDLTypeInt -> "intValue"
            is IDLTypeNat -> "natValue"
            is IDLTypeNat64 -> "nat64Value"
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> "icpPrincipal"
            is IDLTypeText -> "string"
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> TODO()
            is IDLRecord -> TODO()
        }
}