package com.bity.icp_kotlin_kit.domain.model

import com.bity.icp_kotlin_kit.util.cryptography.ICPAccountCryptography

@OptIn(ExperimentalStdlibApi::class)
class ICPAccount(
    val principal: ICPPrincipal,
    val subAccountId: ByteArray
) {
    val accountId = ICPAccountCryptography.generateAccountId(principal, subAccountId)
    val address = accountId.toHexString()

    companion object {

        private const val SUB_ACCOUNT_ID_LENGTH = 32
        val DEFAULT_SUB_ACCOUNT_ID = ByteArray(SUB_ACCOUNT_ID_LENGTH) { 0 }

        fun mainAccount(principal: ICPPrincipal): ICPAccount =
            ICPAccount(
                principal = principal,
                subAccountId = DEFAULT_SUB_ACCOUNT_ID
            )
    }
}