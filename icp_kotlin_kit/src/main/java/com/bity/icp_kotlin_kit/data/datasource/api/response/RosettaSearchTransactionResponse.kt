package com.bity.icp_kotlin_kit.data.datasource.api.response

import com.bity.icp_kotlin_kit.data.datasource.api.response.model.RosettaTransactionApiModel
import com.fasterxml.jackson.annotation.JsonProperty

class RosettaSearchTransactionResponse(
    @JsonProperty(value = "transactions") val transactions: List<RosettaTransactionApiModel>
)