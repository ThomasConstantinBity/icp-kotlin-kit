package com.bity.icp_kotlin_kit.domain.model.error

sealed class DERSerialisationError(
    errorMessage: String? = null
): ICPCryptographyError(errorMessage)

class InvalidEcPublicKey: DERSerialisationError()