package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_kotlin_kit.data.datasource.api.enum.ContentRequestType
import com.fasterxml.jackson.annotation.JsonProperty

internal class ReadStateApiModel(
    requestType: ContentRequestType,
    sender: ByteArray,
    nonce: ByteArray,
    ingressExpiry: Long,
    @JsonProperty("paths") val paths: List<List<ByteArray>>
): ContentApiModel(
    request_type = requestType,
    sender = sender,
    nonce = nonce,
    ingress_expiry = ingressExpiry
)