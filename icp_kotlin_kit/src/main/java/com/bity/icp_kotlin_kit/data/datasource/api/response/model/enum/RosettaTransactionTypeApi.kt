package com.bity.icp_kotlin_kit.data.datasource.api.response.model.enum

import com.fasterxml.jackson.annotation.JsonProperty

enum class RosettaTransactionTypeApi {
    @JsonProperty(value = "TRANSACTION") Transaction,
    @JsonProperty(value = "FEE") Fee
}