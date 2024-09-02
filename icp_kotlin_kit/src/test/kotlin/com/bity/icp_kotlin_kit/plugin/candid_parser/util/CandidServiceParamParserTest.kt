package com.bity.icp_kotlin_kit.plugin.candid_parser.util

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceParam
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_vec.IDLVec
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CandidServiceParamParserTest {

    @Test
    fun test() {
        val input = "vec opt TransferResult"
        val result = CandidServiceParamParser.parseServiceParam(input)
        assertEquals(
            IDLServiceParam(
                params = listOf(
                    IDLTypeVec(
                        vecDeclaration = "vec opt TransferResult"
                    )
                )
            ),
            result
        )
    }

    @Test
    fun test2() {
        val input = "vec TransferArg"
        val result = CandidServiceParamParser.parseServiceParam(input)
        assertEquals(
            IDLServiceParam(
                params = listOf(
                    IDLTypeVec(
                        vecDeclaration = "vec TransferArg"
                    )
                )
            ),
            result
        )
    }
}