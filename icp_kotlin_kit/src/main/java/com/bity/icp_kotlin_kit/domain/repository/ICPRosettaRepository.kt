package com.bity.icp_kotlin_kit.domain.repository

import com.bity.icp_kotlin_kit.domain.model.RosettaTransaction

interface ICPRosettaRepository {
    suspend fun accountTransactions(address: String): Result<List<RosettaTransaction>>
}