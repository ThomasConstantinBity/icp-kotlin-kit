package com.bity.icp_kotlin_kit.data.datasource.api.response

import com.fasterxml.jackson.annotation.JsonProperty

class ReadStateResponse(
    @JsonProperty("certificate") val certificate: ByteArray
)