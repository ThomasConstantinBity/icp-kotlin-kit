package com.bity.icp_kotlin_kit.util.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type

internal class JacksonResponseBodyConverter<T>(
    private val mapper: ObjectMapper,
    private val type: Type
) : Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody): T? {
        value.use { v ->
            val javaType = mapper.typeFactory.constructType(type)
            return mapper.readValue(v.bytes(), javaType)
        }
    }
}