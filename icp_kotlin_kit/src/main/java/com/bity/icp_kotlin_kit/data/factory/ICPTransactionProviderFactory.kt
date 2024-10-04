package com.bity.icp_kotlin_kit.data.factory

import com.bity.icp_kotlin_kit.data.remote.transaction_provider.ICPICRC1IndexTransactionProvider
import com.bity.icp_kotlin_kit.data.remote.transaction_provider.ICPIndexTransactionProvider
import com.bity.icp_kotlin_kit.domain.provider.ICPTransactionProvider
import com.bity.icp_kotlin_kit.domain.generated_file.NNS_SNS_W
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.model.ICPToken
import com.bity.icp_kotlin_kit.domain.model.enum.ICPSystemCanisters
import com.bity.icp_kotlin_kit.provideNNSSNSWService

internal class ICPTransactionProviderFactory private constructor(
    private val service: NNS_SNS_W.nns_sns_wService
) {

    constructor(): this(provideNNSSNSWService())

    private var cachedSNSes: List<NNS_SNS_W.DeployedSns> = emptyList()

    suspend fun getTransactionProvider(token: ICPToken): ICPTransactionProvider? {
        // TODO: Support DIP20 tokens
        if(token.canister == ICPSystemCanisters.Ledger.icpPrincipal)
            return ICPIndexTransactionProvider(token)
        val index = findIndexCanisterInSNS(token.canister)
            ?: return null
        return ICPICRC1IndexTransactionProvider(
            icpToken = token,
            indexCanister = index
        )
    }

    private suspend fun findIndexCanisterInSNS(tokenCanister: ICPPrincipal): ICPPrincipal? {
        val deployed = deployedSNSes()
        return deployed.firstOrNull {
            it.root_canister_id == tokenCanister
                    || it.governance_canister_id == tokenCanister
                    || it.index_canister_id == tokenCanister
                    || it.swap_canister_id == tokenCanister
                    || it.ledger_canister_id == tokenCanister
        }?.index_canister_id
    }

    private suspend fun deployedSNSes(): List<NNS_SNS_W.DeployedSns> {
        if(cachedSNSes.isNotEmpty()) return cachedSNSes
        cachedSNSes = service.list_deployed_snses().instances.toList()
        return cachedSNSes
    }
}