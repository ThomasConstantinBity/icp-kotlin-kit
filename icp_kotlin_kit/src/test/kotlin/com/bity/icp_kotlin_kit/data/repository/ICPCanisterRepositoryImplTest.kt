package com.bity.icp_kotlin_kit.data.repository

import com.bity.icp_candid.domain.model.CandidDictionary
import com.bity.icp_candid.domain.model.CandidValue
import com.bity.icp_kotlin_kit.data.datasource.api.service.ICPRetrofitService
import com.bity.icp_kotlin_kit.domain.model.ICPMethod
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class ICPCanisterRepositoryImplTest {

    private val baseUrl: String = "https://icp-api.io/api/v2/canister/"
    private val cborFactory = CBORFactory()
    private val objectMapper = ObjectMapper(cborFactory)

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun test() = runTest {

        val client: ICPRetrofitService =
            Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .client(
                    OkHttpClient()
                        .newBuilder()
                        .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
                        .build()
                )
                .addConverterFactory(
                    JacksonConverterFactory.create(objectMapper.apply { configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) })
                )
                .build()
                .create(ICPRetrofitService::class.java)

        ICPCanisterRepositoryImpl(client).query(
            method = ICPMethod(
                canister = ICPPrincipal.init("ryjl3-tyaaa-aaaaa-aaaba-cai"),
                methodName = "account_balance",
                args = CandidValue.Record(
                    CandidDictionary(
                        hashMapOf(
                            "account" to CandidValue.Blob("cafd0a2c27f41a851837b00f019b93e741f76e4147fe74435fb7efb836826a1c".hexToByteArray())
                        )
                    )
                )
            ),
        )
    }
}