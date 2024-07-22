package com.bity.icp_kotlin_kit.data.datasource.api.response.model

import com.bity.icp_kotlin_kit.data.datasource.api.response.model.enum.RosettaTransactionTypeApi
import com.fasterxml.jackson.annotation.JsonProperty

class RosettaTransactionOperationApiModel(
    @JsonProperty(value = "operation_identifier") val operationIdentifier: RosettaOperationIdentifierApiModel,
    @JsonProperty(value = "type") val type: RosettaTransactionTypeApi,
    @JsonProperty(value = "account") val account: RosettaTransactionAccountApiModel,
    @JsonProperty(value = "amount") val amount: RosettaTransactionAmountApiModel
)