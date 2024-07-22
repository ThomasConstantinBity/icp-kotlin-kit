package com.bity.icp_cryptography.model.error

sealed class DERSerialisationError(
    errorMessage: String? = null
): ICPCryptographyError(errorMessage)

class InvalidEcPublicKey: DERSerialisationError()