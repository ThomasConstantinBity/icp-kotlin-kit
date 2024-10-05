package com.bity.icp_kotlin_kit.domain.model.error

import com.bity.icp_kotlin_kit.domain.model.ICPToken

class GetAllTransactionsException(
    token: ICPToken,
    errorMessage: String?
): Exception("Error while fetching transactions for ${token.name}: $errorMessage")