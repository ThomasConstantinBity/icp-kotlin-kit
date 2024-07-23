package com.bity.icp_kotlin_kit.data.datasource.api.request

import com.bity.icp_kotlin_kit.data.datasource.api.model.ContentApiModel
import com.fasterxml.jackson.annotation.JsonProperty

internal class ICPRequestEnvelope(
    @field:JsonProperty("content") val content: ContentApiModel,
    @field:JsonProperty("sender_pubkey") val senderPubKey: ByteArray? = null,
    @field:JsonProperty("sender_sig") val senderSig: ByteArray? = null
)