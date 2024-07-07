package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_kotlin_kit.data.datasource.api.enum.RequestType
import com.fasterxml.jackson.annotation.JsonProperty

abstract class ContentApiModel(
    @JsonProperty("request_type") val requestType: RequestType,
    @JsonProperty("sender") val sender: ByteArray,
    @JsonProperty("nonce") val nonce: ByteArray,
    @JsonProperty("ingress_expiry") val ingressExpiry: Long
)