package com.bity.icp_kotlin_kit.util.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Converter

internal class JacksonRequestBodyConverter<T>(
    private val mapper: ObjectMapper,
) : Converter<T, RequestBody> {
    override fun convert(value: T): RequestBody = mapper.writeValueAsBytes(value).toRequestBody()
}