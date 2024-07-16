package com.bity.icp_kotlin_kit.data.model

sealed class RemoteClientError(errorMessage: String? = null): Error(errorMessage) {
    class HttpError(errorCode: Int, errorMessage: String?): RemoteClientError("$errorCode - ${errorMessage ?: ""}")
    class MissingBody: RemoteClientError()

    @OptIn(ExperimentalStdlibApi::class)
    class ParsingError(arg: ByteArray): RemoteClientError("Unable to parse arg: ${arg.toHexString()}")
}