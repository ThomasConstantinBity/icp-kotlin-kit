package com.bity.icp_kotlin_kit.domain.usecase.token

import com.bity.icp_kotlin_kit.domain.model.ICPToken
import com.bity.icp_kotlin_kit.domain.repository.TokenRepository
import com.bity.icp_kotlin_kit.provideTokenRepository
import java.math.BigInteger

class GetTokenFeeUseCase private constructor(
    private val tokenRepository: TokenRepository
){
    constructor(): this(provideTokenRepository())

    suspend operator fun invoke(token: ICPToken): BigInteger =
        tokenRepository.fee(token)
}