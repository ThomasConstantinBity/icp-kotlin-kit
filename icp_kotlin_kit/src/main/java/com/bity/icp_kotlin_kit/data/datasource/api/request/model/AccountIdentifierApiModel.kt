package com.bity.icp_kotlin_kit.data.datasource.api.request.model

import com.fasterxml.jackson.annotation.JsonProperty

class AccountIdentifierApiModel(
    @JsonProperty("address") val address: String
)