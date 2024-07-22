package com.bity.icp_kotlin_kit.data.datasource.api.request.model

import com.fasterxml.jackson.annotation.JsonProperty

class NetworkIdentifierApiModel(
    @JsonProperty("blockchain") val blockchain: String,
    @JsonProperty("network") val network: String
)