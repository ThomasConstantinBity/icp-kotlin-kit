package com.bity.icp_kotlin_kit.data.datasource.api.response.model

import com.fasterxml.jackson.annotation.JsonProperty

class ReplyApiModel(
    @JsonProperty("arg") val arg: ByteArray
)