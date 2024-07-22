package com.bity.icp_kotlin_kit.data.datasource.api.response.model

import com.fasterxml.jackson.annotation.JsonProperty

class RosettaTransactionDetailsApiModel(
    @JsonProperty(value = "transaction_identifier") val transactionIdentifier: RosettaTransactionIdentifierApiModel,
    @JsonProperty(value = "operations") val operations: List<RosettaTransactionOperationApiModel>,
    @JsonProperty(value = "metadata") val metadata: RosettaTransactionMetadataApiModel
)