package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec

internal object IDLTypeVecHelper {

    internal fun kotlinDefinition(className: String) = "Array<$className>"

}