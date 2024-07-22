package com.bity.icp_kotlin_kit.data.datasource.api.response.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

class RosettaBlockIdentifierApiModel(
    @JsonProperty(value = "index") val index: BigInteger,
    @JsonProperty(value = "hash") val hash: String
)