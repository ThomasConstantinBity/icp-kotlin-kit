package com.bity.icp_kotlin_kit.domain.usecase.token

import com.bity.icp_kotlin_kit.domain.model.ICPTokenTransfer
import com.bity.icp_kotlin_kit.domain.model.arg.ICPTokenTransferArgs
import com.bity.icp_kotlin_kit.domain.repository.TokenRepository
import com.bity.icp_kotlin_kit.provideTokenRepository

class TransferTokenUseCase private constructor(
    private val tokenRepository: TokenRepository
) {
    constructor(): this(
        tokenRepository = provideTokenRepository()
    )

    suspend operator fun invoke(transferArgs: ICPTokenTransferArgs): ICPTokenTransfer =
        tokenRepository.send(transferArgs)
}