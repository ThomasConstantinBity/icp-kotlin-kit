package com.bity.icp_kotlin_kit.domain.model.error

sealed class ICPLedgerCanisterError(errorMessage: String? = null): Error(errorMessage) {
    class InvalidResponse: ICPLedgerCanisterError()
    class InvalidReceivingAddress: ICPLedgerCanisterError()

}