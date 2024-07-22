package com.bity.icp_candid.domain.model.error

sealed class CandidDeserializationError: Error() {
    class InvalidPrefix: CandidDeserializationError()
    class InvalidPrimitive: CandidDeserializationError()
    class InvalidTypeReference: CandidDeserializationError()
    class InvalidUTF8String: CandidDeserializationError()
    class UnSerializedBytesLeft: CandidDeserializationError()
}