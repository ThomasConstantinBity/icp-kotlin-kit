package com.bity.icp_kotlin_kit.data.datasource.api.request

import com.bity.icp_kotlin_kit.data.datasource.api.model.ContentApiModel
import com.fasterxml.jackson.annotation.JsonProperty

class ICPRequestEnvelope(
    @JsonProperty("content") val content: ContentApiModel,
    @JsonProperty("sender_pubkey") val senderPubKey: ByteArray? = null,
    @JsonProperty("sender_sig") val senderSig: ByteArray? = null
)