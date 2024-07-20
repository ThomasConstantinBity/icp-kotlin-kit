package com.bity.icp_kotlin_kit.domain.model.error

sealed class ICPAccountError(errorMessage: String? = null): Error(errorMessage) {
    class InvalidSubAccountId(errorMessage: String? = null): ICPAccountError(errorMessage)
}