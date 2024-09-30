package com.bity.icp_kotlin_kit.data.model

sealed class DABTokenException(errorMessage: String? = null): Exception(errorMessage) {
    class InvalidType(type: String):
        DABTokenException("Missing field: $type")
    class WrongTokenStandard(tokenStandard: String):
        DABTokenException("Token standard not supported: $tokenStandard")
}