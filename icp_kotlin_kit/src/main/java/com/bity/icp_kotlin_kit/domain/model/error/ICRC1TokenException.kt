package com.bity.icp_kotlin_kit.domain.model.error

sealed class ICRC1TokenException(errorMessage: String? = null): Exception(errorMessage) {
    class InvalidMetadataField(key: String): ICRC1TokenException(
        "Invalid metadata field: $key"
    )
}