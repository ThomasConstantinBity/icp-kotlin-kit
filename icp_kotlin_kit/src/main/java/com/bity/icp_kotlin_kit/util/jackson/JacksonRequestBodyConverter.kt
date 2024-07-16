package com.bity.icp_kotlin_kit.util.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.RequestBody
import retrofit2.Converter
import java.lang.reflect.Type

internal class JacksonRequestBodyConverter<T>(
    private val mapper: ObjectMapper,
    private val type: Type
) : Converter<T, RequestBody> {
    override fun convert(value: T): RequestBody {
        val javaType = mapper.typeFactory.constructType(type)
        return RequestBody.create(null, mapper.writeValueAsBytes(value))
    }
}