package com.bity.icp_kotlin_kit.data.repository

import com.bity.icp_kotlin_kit.provideICPRosettaRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ICPRosettaRepositoryImplTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun test() = runTest {
        val repository = provideICPRosettaRepository(true)
        val transactions = repository.searchTransactions("cafd0a2c27f41a851837b00f019b93e741f76e4147fe74435fb7efb836826a1c")

        val transaction = transactions.getOrThrow()
            .find {
                it.hash.contentEquals("cfb4a74a598a173a4388d4ff6c8f7b777d35e03df2b1bf60dd983745672164d7".hexToByteArray())
            }
        assertNotNull(transaction)
        assertEquals(
            "c529d0eff49b66d6d592d876cb7d9a2ffe3faeca3546e6c2e2b2aa2752ff63c0",
            transaction.type.from
        )
        assertEquals(
            "cafd0a2c27f41a851837b00f019b93e741f76e4147fe74435fb7efb836826a1c",
            transaction.type.to
        )
        assertEquals(
            BigInteger("1000000"),
            transaction.amount
        )
        assertEquals(
            BigInteger("10000"),
            transaction.fee
        )
    }
}