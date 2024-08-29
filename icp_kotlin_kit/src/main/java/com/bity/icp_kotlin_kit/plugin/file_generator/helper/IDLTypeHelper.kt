package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFuncDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec

internal object IDLTypeHelper {
    fun kotlinTypeVariable(type: IDLType): String =
        when(type) {
            is IDLFun -> TODO()
            is IDLTypeBlob -> "ByteArray"
            is IDLTypeBoolean -> "Boolean"
            is IDLTypeCustom -> type.typeDef
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeInt -> "Int"
            is IDLTypeNat -> "UInt"
            is IDLTypeNat64 -> "ULong"
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> "ICPPrincipal"
            is IDLTypeRecord -> TODO()
            is IDLTypeText -> "String"
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> {
                val idlVec = CandidVecParser.parseVec(type.vecDeclaration)
                val typeArray = StringBuilder(kotlinTypeVariable(idlVec.type))
                if (idlVec.isOptional) typeArray.append("?")
                "Array<$typeArray>"
            }
        }
}