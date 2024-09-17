package com.bity.icp_kotlin_kit

import com.bity.icp_kotlin_kit.data.datasource.api.service.ICPRetrofitService
import com.bity.icp_kotlin_kit.data.datasource.api.service.ICPRosettaService
import com.bity.icp_kotlin_kit.data.repository.ICPCanisterRepositoryImpl
import com.bity.icp_kotlin_kit.data.repository.ICPRosettaRepositoryImpl
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.bity.icp_kotlin_kit.domain.repository.ICPRosettaRepository
import com.bity.icp_kotlin_kit.util.jackson.CborConverterFactory
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

private const val BASE_URL: String = "https://icp-api.io/api/v2/canister/"
private const val ROSETTA_BASE_URL = "https://rosetta-api.internetcomputer.org/"

private val objectMapper = ObjectMapper(CBORFactory())
private val cborConverterFactory = CborConverterFactory.create(
    objectMapper.apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
)

private val rosettaConverterFactory = JacksonConverterFactory.create(
    jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
)

private val httpClient= OkHttpClient().newBuilder().build()

internal fun provideICPCanisterRepository(): ICPCanisterRepository =
    ICPCanisterRepositoryImpl(
        icpRetrofitService = provideICPRetrofitService(),
    )

private fun provideICPRetrofitService(): ICPRetrofitService =
    Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(cborConverterFactory)
        .build()
        .create(ICPRetrofitService::class.java)

/**
 * Rosetta
 */
internal fun provideICPRosettaRepository(): ICPRosettaRepository =
    ICPRosettaRepositoryImpl(
        client = provideICPRosettaService()
    )
private fun provideICPRosettaService(): ICPRosettaService =
    Retrofit
        .Builder()
        .baseUrl(ROSETTA_BASE_URL)
        .client(httpClient)
        .addConverterFactory(rosettaConverterFactory)
        .build()
        .create(ICPRosettaService::class.java)