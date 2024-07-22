package com.bity.icp_kotlin_kit.data.datasource.api.response.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

class RosettaTransactionAmountApiModel(
    @JsonProperty(value = "value") val value: BigInteger,
)