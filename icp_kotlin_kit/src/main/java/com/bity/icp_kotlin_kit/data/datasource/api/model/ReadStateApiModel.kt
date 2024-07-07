package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_kotlin_kit.data.datasource.api.enum.RequestType
import com.fasterxml.jackson.annotation.JsonProperty

class ReadStateApiModel(
    requestType: RequestType,
    sender: ByteArray,
    nonce: ByteArray,
    ingressExpiry: Long,
    @JsonProperty("paths") val paths: List<List<ByteArray>>
): ContentApiModel(
    requestType = requestType,
    sender = sender,
    nonce = nonce,
    ingressExpiry = ingressExpiry
)