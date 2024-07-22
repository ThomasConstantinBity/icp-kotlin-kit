package com.bity.icp_kotlin_kit

import com.bity.icp_kotlin_kit.data.datasource.api.service.ICPRetrofitService
import com.bity.icp_kotlin_kit.data.datasource.api.service.ICPRosettaService
import com.bity.icp_kotlin_kit.data.repository.ICPCanisterRepositoryImpl
import com.bity.icp_kotlin_kit.data.repository.ICPRosettaRepositoryImpl
import com.bity.icp_kotlin_kit.domain.repository.ICPCanisterRepository
import com.bity.icp_kotlin_kit.domain.repository.ICPRosettaRepository
import com.bity.icp_kotlin_kit.domain.usecase.ICPLedgerCanisterUseCase
import com.bity.icp_kotlin_kit.util.jackson.CborConverterFactory
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
private val loggedHttpClient= OkHttpClient().newBuilder()
    .addInterceptor(
        HttpLoggingInterceptor()
            .apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
    )
    .build()

fun provideICPLedgerCanisterUseCase(debug: Boolean): ICPLedgerCanisterUseCase =
    ICPLedgerCanisterUseCase(
        icpCanisterRepository = provideICPCanisterRepository(debug)
    )

private fun provideICPCanisterRepository(debug: Boolean): ICPCanisterRepository =
    ICPCanisterRepositoryImpl(
        icpRetrofitService = if(debug)
            provideLoggedICPRetrofitService()
        else provideICPRetrofitService()
    )

private fun provideLoggedICPRetrofitService(): ICPRetrofitService =
    Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(loggedHttpClient)
        .addConverterFactory(cborConverterFactory)
        .build()
        .create(ICPRetrofitService::class.java)

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
internal fun provideICPRosettaRepository(debug: Boolean): ICPRosettaRepository =
    ICPRosettaRepositoryImpl(
        client = if(debug) provideLoggedICPRosettaService() else provideICPRosettaService()
    )
private fun provideICPRosettaService(): ICPRosettaService =
    Retrofit
        .Builder()
        .baseUrl(ROSETTA_BASE_URL)
        .client(httpClient)
        .addConverterFactory(rosettaConverterFactory)
        .build()
        .create(ICPRosettaService::class.java)

private fun provideLoggedICPRosettaService(): ICPRosettaService =
    Retrofit
        .Builder()
        .baseUrl(ROSETTA_BASE_URL)
        .client(loggedHttpClient)
        .addConverterFactory(rosettaConverterFactory)
        .build()
        .create(ICPRosettaService::class.java)