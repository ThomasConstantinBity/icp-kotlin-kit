package com.bity.icp_kotlin_kit.data.repository

import com.bity.icp_kotlin_kit.data.model.RemoteClientError
import com.bity.icp_kotlin_kit.data.remote.ICPTokenActorFactory
import com.bity.icp_kotlin_kit.domain.generated_file.Tokens
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPToken
import com.bity.icp_kotlin_kit.domain.model.ICPTokenTransfer
import com.bity.icp_kotlin_kit.domain.model.arg.ICPTokenTransferArgs
import com.bity.icp_kotlin_kit.domain.model.enum.ICPTokenStandard
import com.bity.icp_kotlin_kit.domain.model.error.TokenActorException
import com.bity.icp_kotlin_kit.domain.repository.TokenRepository
import java.math.BigInteger

class TokenRepositoryImpl(
    private val tokensService: Tokens.TokensService
): TokenRepository {

    private var cachedTokens: List<ICPToken>? = null

    override suspend fun getAllTokens(): List<ICPToken> {
        cachedTokens?.let { return it }
        val tokens = tokensService.get_all()
            .map { ICPToken(it) }
        cachedTokens = tokens
        return tokens
    }

    override suspend fun getTokenBalance(
        standard: ICPTokenStandard,
        canister: ICPPrincipal,
        principal: ICPPrincipal
    ): BigInteger? {
        val actor = ICPTokenActorFactory.createActor(standard, canister)
            ?: return null
        return try {
            actor.getBalance(principal)
        } catch (_: RemoteClientError) { null }
    }

    override suspend fun fee(token: ICPToken): BigInteger {
        val actor = ICPTokenActorFactory.createActor(
            standard = token.standard,
            canister = token.canister
        ) ?: throw TokenActorException.NullActorException(
            standard = token.standard,
            canister = token.canister
        )
        return actor.fee()
    }

    override suspend fun send(transferArgs: ICPTokenTransferArgs): ICPTokenTransfer {
        val actor = ICPTokenActorFactory.createActor(
            standard = transferArgs.token.standard,
            canister = transferArgs.token.canister
        ) ?: throw TokenActorException.NullActorException(
            standard = transferArgs.token.standard,
            canister = transferArgs.token.canister
        )
        return actor.transfer(transferArgs)
    }
}