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
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName

internal object IDLTypeHelper {

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
                TODO()
                /*if (className != null) "$className.${type.typeDef}"
                else type.typeDef*/
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
            is IDLTypeVec -> {
                TODO()
                /*val idlVec = CandidVecParser.parseVec(type.vecDeclaration)
                val typeArray = StringBuilder(
                    kotlinTypeVariable(
                        type = idlVec.type,
                        className = className
                    )
                )
                if (idlVec.isOptional) typeArray.append("?")
                if(className != "Array") "Array<$typeArray>" else "kotlin.Array<$typeArray>"*/
            }
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