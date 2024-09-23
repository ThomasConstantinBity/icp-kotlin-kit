package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFloat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt16
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt32
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat16
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat32
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat8
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

    fun kotlinVariableName(
        type: IDLType,
        className: String?
    ): String {
        return kotlinTypeVariable(
            type = type,
            className = className
        ).replaceFirstChar { it.lowercase() }
    }

    // TODO, can remove className ?
    fun kotlinTypeVariable(
        type: IDLType,
        className: String? = null
    ): String =
        when (type) {
            is IDLFun -> "${mapInputFunTypeVariable(type, className)} -> ${mapOutputFunTypeVariable(type, className)}"
            is IDLTypeBlob -> "ByteArray"
            is IDLTypeBoolean -> "Boolean"
            is IDLTypeCustom -> {
                requireNotNull(type.typeDef)
                when {
                    className != null -> "$className.${type.typeDef}"
                    else -> type.typeDef
                }
            }

            is IDLTypeInt -> "BigInteger"
            is IDLTypeInt16 -> "Short"
            is IDLTypeInt32 -> "Int"
            is IDLTypeInt64 -> "Long"

            is IDLTypeNat -> "BigInteger"
            is IDLTypeNat8 -> "UByte"
            is IDLTypeNat16 -> "UShort"
            is IDLTypeNat32 -> "UInt"
            is IDLTypeNat64 -> "ULong"

            is IDLTypeFloat64 -> "Double"

            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> "ICPPrincipal"
            is IDLRecord -> className
                ?: throw RuntimeException("className is required for record declaration '${type}'")

            is IDLTypeText -> "String"
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> "Array<${kotlinTypeVariable(type.vecType, className)}${if(type.isOptional) "?" else ""}>"
        }

    private fun mapInputFunTypeVariable(idlFun: IDLFun, className: String?): String =
        idlFun.inputArgs.joinToString(
            prefix = "(",
            postfix = ")"
        ) { kotlinTypeVariable(it, className) }

    private fun mapOutputFunTypeVariable(idlFun: IDLFun, className: String?): String {
        return if(idlFun.outputArgs.isEmpty()) "Unit"
        else idlFun.outputArgs.joinToString(
            prefix = "(",
            postfix = ")"
        ) { kotlinTypeVariable(it, className) }
    }
}