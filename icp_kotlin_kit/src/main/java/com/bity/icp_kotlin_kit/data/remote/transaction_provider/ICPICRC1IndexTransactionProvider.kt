package com.bity.icp_kotlin_kit.data.remote.transaction_provider

import com.bity.icp_kotlin_kit.domain.model.ICPAccount
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPToken
import com.bity.icp_kotlin_kit.domain.model.token_transaction.ICPTokenTransaction
import com.bity.icp_kotlin_kit.domain.provider.ICPTransactionProvider

class ICPICRC1IndexTransactionProvider(
    private val icpToken: ICPToken,
    private val indexCanister: ICPPrincipal
): ICPTransactionProvider {

    override suspend fun getAllTransactions(account: ICPAccount): List<ICPTokenTransaction> {
        val transactions = TODO()
    }
}