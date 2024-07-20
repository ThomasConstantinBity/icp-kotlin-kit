package com.bity.icp_kotlin_kit.domain.model

import com.bity.icp_kotlin_kit.util.cryptography.ICPAccountCryptography

@OptIn(ExperimentalStdlibApi::class)
class ICPAccount(
    principal: ICPPrincipal,
    val subAccountId: ByteArray
) {
    val accountId: ByteArray = ICPAccountCryptography.generateAccountId(principal, subAccountId)
    val address = accountId.toHexString()

    companion object {
        fun mainAccount(principal: ICPPrincipal): ICPAccount =
            ICPAccount(
                principal = principal,
                subAccountId = ByteArray(32) { 0 }
            )
    }
}