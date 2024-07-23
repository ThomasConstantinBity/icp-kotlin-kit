package com.bity.icp_kotlin_kit.data.model

sealed class ParsingError(errorMessage: String? = null): Error(errorMessage) {
    class InvalidCertificateStructure(errorMessage: String? = null): ParsingError(errorMessage)
}