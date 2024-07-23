package com.bity.icp_kotlin_kit.data.datasource.api.response.model.enum

import com.fasterxml.jackson.annotation.JsonProperty

enum class RejectCodeApiModel {
    @JsonProperty("1") SystemFatal,
    @JsonProperty("2") SystemTransient,
    @JsonProperty("3") DestinationInvalid,
    @JsonProperty("4") CanisterReject,
    @JsonProperty("5") CanisterError;

    companion object {
        fun valueFromErrorCode(value: Int): RejectCodeApiModel? =
            when(value) {
                1 -> SystemFatal
                2 -> SystemTransient
                3 -> DestinationInvalid
                4 -> CanisterReject
                5 -> CanisterError
                else -> null
            }
    }
}