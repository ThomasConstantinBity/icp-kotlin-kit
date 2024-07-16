package com.bity.icp_kotlin_kit.domain.model.error

sealed class ApiModelError(
    errorMessage: String? = null,
    throwable: Throwable? = null
): Error(errorMessage, throwable) {
    class NoMethodForReadState: ApiModelError()
}