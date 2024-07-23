package com.bity.icp_kotlin_kit.data.datasource.api.response

import com.bity.icp_kotlin_kit.data.datasource.api.response.model.ReplyApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.response.model.enum.RejectCodeApiModel
import com.bity.icp_kotlin_kit.data.datasource.api.response.model.enum.StatusCodeApiModel
import com.fasterxml.jackson.annotation.JsonProperty

class QueryResponse(
    @JsonProperty("status") val status: StatusCodeApiModel,
    @JsonProperty("reply") val reply: ReplyApiModel?,
    @JsonProperty("reject_code") val rejectCode: RejectCodeApiModel?,
    @JsonProperty("reject_message") val rejectMessage: String?,
    @JsonProperty("error_code") val errorCode: String?
)