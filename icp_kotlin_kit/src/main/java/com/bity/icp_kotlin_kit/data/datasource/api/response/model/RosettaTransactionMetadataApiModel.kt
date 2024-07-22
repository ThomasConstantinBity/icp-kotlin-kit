package com.bity.icp_kotlin_kit.data.datasource.api.response.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

class RosettaTransactionMetadataApiModel(
    @JsonProperty(value = "block_height") val blockHeight: BigInteger,
    @JsonProperty(value = "memo") val memo: BigInteger,
    @JsonProperty(value = "timestamp") val timestamp: Long
)