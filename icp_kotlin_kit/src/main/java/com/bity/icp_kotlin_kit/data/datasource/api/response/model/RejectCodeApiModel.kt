package com.bity.icp_kotlin_kit.data.datasource.api.response.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class RejectCodeApiModel {
    @JsonProperty("1") SystemFatal,
    @JsonProperty("2") SystemTransient,
    @JsonProperty("3") DestinationInvalid,
    @JsonProperty("4") CanisterReject,
    @JsonProperty("5") CanisterError
}