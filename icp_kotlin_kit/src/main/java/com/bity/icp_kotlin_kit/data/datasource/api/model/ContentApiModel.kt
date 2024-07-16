package com.bity.icp_kotlin_kit.data.datasource.api.model

import com.bity.icp_kotlin_kit.data.datasource.api.enum.ContentRequestType
import com.bity.icp_kotlin_kit.util.OrderIndependentHash
import com.fasterxml.jackson.annotation.JsonProperty

// Need to use sneak case because of order independent hash
internal abstract class ContentApiModel(
    val request_type: ContentRequestType,
    val sender: ByteArray,
    val nonce: ByteArray,
    val ingress_expiry: Long
) {
    fun calculateRequestId(): ByteArray = OrderIndependentHash(this)
}