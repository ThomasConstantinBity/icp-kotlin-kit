package com.bity.icp_kotlin_kit.domain.model

import com.bity.icp_kotlin_kit.domain.model.block_query_candid_response.ICPBlockOperation

sealed class ICPTransactionType(val from : String?, val to: String?) {
    class Mint(to: String): ICPTransactionType(null, to)
    class Burn(from: String): ICPTransactionType(from, null)
    class Send(from: String, to: String): ICPTransactionType(from, to)

    companion object {

        @OptIn(ExperimentalStdlibApi::class)
        fun init(operation: ICPBlockOperation): ICPTransactionType =
            when (operation) {
                is ICPBlockOperation.Burn ->
                    Burn(operation.form.toHexString())

                is ICPBlockOperation.Mint ->
                    Mint(operation.to.toHexString())

                is ICPBlockOperation.Transfer ->
                    Send(
                        from = operation.from.toHexString(),
                        to = operation.to.toHexString()
                    )
            }
    }
}