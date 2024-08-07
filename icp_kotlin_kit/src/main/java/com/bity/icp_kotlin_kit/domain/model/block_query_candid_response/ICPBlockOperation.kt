package com.bity.icp_kotlin_kit.domain.model.block_query_candid_response

import com.bity.icp_kotlin_kit.candid.model.CandidDictionary
import com.bity.icp_kotlin_kit.candid.model.CandidValue
import com.bity.icp_kotlin_kit.domain.model.error.ICPLedgerCanisterError
import com.bity.icp_kotlin_kit.util.ext_function.ICPAmount

sealed class ICPBlockOperation(
    val amount: ULong,
    val fee: ULong? = null
) {

    class Burn(
        val form: ByteArray,
        amount: ULong
    ): ICPBlockOperation(amount)

    class Mint(
        val to: ByteArray,
        amount: ULong
    ): ICPBlockOperation(amount)

    class Transfer(
        val from: ByteArray,
        val to: ByteArray,
        amount: ULong,
        fee: ULong
    ): ICPBlockOperation(amount, fee)

    companion object {

        internal fun init(value: CandidValue): ICPBlockOperation {
            val candidValue = value.optionValue?.value ?: value
            val operation = candidValue.variantValue
                ?: throw ICPLedgerCanisterError.InvalidResponse()
            return when (operation.hashedKey) {

                CandidDictionary.hash("Mint") -> {
                    val transfer = operation.value.recordValue
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    val to = transfer["to"]?.blobValue
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    val amount = transfer["amount"]?.ICPAmount
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    Mint(to, amount)
                }

                CandidDictionary.hash("Burn") -> {
                    val transfer = operation.value.recordValue
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    val from = transfer["from"]?.blobValue
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    val amount = transfer["amount"]?.ICPAmount
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    Burn(from, amount)
                }

                CandidDictionary.hash("Transfer") -> {
                    val transfer = operation.value.recordValue
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    val from = transfer["from"]?.blobValue
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    val to = transfer["to"]?.blobValue
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    val amount = transfer["amount"]?.ICPAmount
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    val fee = transfer["fee"]?.ICPAmount
                        ?: throw ICPLedgerCanisterError.InvalidResponse()
                    Transfer(from, to, amount, fee)
                }

                else -> throw ICPLedgerCanisterError.InvalidResponse()
            }
        }
    }
}