package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_kotlin_kit.data.datasource.api.enum.ContentRequestType
import com.fasterxml.jackson.annotation.JsonProperty

// Need to use sneak case because of order independent hash
internal class CallApiModel(
    requestType: ContentRequestType,
    sender: ByteArray,
    nonce: ByteArray,
    ingressExpiry: Long,
    val method_name: String,
    val canister_id: ByteArray,
    val arg: ByteArray
): ContentApiModel(
    request_type = requestType,
    sender = sender,
    nonce = nonce,
    ingress_expiry = ingressExpiry
)