package com.bity.icp_kotlin_kit.plugin.file_generator.helper

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLServiceType
import org.junit.jupiter.api.Test

class IDLServiceHelperTest {

    @Test
    fun test() {
        val idlService = IDLService(
            comment = null,
            id = "icrc7_collection_metadata",
            inputParamsDeclaration = "",
            outputParamsDeclaration = "vec record { text; Value }",
            serviceType = IDLServiceType.Query
        )
        println(IDLServiceHelper(idlService).convertServiceIntoKotlinFunction())
    }
}