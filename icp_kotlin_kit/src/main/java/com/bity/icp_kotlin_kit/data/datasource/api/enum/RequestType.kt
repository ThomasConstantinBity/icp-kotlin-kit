package com.bity.icp_kotlin_kit.data.datasource.api.enum

import com.fasterxml.jackson.annotation.JsonProperty

enum class RequestType(val type: String) {
    @JsonProperty("call") Call("call"),
    @JsonProperty("query") Query("query"),
    @JsonProperty("read_state") ReadState("read_state")
}