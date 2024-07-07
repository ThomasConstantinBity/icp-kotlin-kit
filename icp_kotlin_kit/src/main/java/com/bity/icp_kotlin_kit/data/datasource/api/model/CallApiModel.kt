package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_kotlin_kit.data.datasource.api.enum.RequestType
import com.fasterxml.jackson.annotation.JsonProperty

class CallApiModel(
    requestType: RequestType,
    sender: ByteArray,
    nonce: ByteArray,
    ingressExpiry: Long,
    @JsonProperty("method_name") val methodName: String,
    @JsonProperty("canister_id") val canisterId: ByteArray,
    @JsonProperty("arg") val arg: ByteArray
): ContentApiModel(
    requestType = requestType,
    sender = sender,
    nonce = nonce,
    ingressExpiry = ingressExpiry
)