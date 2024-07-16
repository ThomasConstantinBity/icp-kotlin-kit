package com.bity.icp_kotlin_kit.data.model

internal sealed class DataError(
    errorMessage: String? = null, throwable: Throwable? = null
) : Error(errorMessage, throwable) {
    class NoResponseData : DataError()
    class HttpError(errorCode: Int, errorMessage: String?) :
        DataError("errorCode: $errorCode, errorMessage: '${errorMessage}'")
}