package com.bity.icp_kotlin_kit.data.datasource.api.response.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class StatusCodeApiModel {
    @JsonProperty(value = "received") Received,
    @JsonProperty(value = "processing") Processing,
    @JsonProperty(value = "replied") Replied,
    @JsonProperty(value = "rejected") Rejected,
    @JsonProperty(value = "done") Done
}