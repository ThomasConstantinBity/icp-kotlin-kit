package com.bity.icp_kotlin_kit.data.datasource.api.enum

import com.bity.icp_kotlin_kit.data.datasource.api.model.ICPRequestApiModel
import com.fasterxml.jackson.annotation.JsonProperty

internal enum class ContentRequestType(val type: String) {
    @JsonProperty("call") Call("call"),
    @JsonProperty("query") Query("query"),
    @JsonProperty("read_state") ReadState("read_state");

    companion object {
        internal fun fromICPRequestApiModel(request: ICPRequestApiModel): ContentRequestType =
            when(request) {
                is ICPRequestApiModel.Call -> Call
                is ICPRequestApiModel.Query -> Query
                is ICPRequestApiModel.ReadState -> ReadState
            }
    }
}