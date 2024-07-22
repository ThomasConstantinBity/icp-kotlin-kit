package com.bity.icp_kotlin_kit.data.datasource.api.request

import com.bity.icp_kotlin_kit.data.datasource.api.request.model.AccountIdentifierApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.request.model.NetworkIdentifierApiModel
import com.fasterxml.jackson.annotation.JsonProperty

class RosettaSearchTransactionRequest(
    @JsonProperty(value = "network_identifier") val networkIdentifier: NetworkIdentifierApiModel,
    @JsonProperty(value = "account_identifier") val accountIdentifier: AccountIdentifierApiModel
)