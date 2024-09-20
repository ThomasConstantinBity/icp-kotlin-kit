package com.bity.demo_app.ui.tokens_balance

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bity.icp_kotlin_kit.domain.model.ICPPrincipal
import com.bity.icp_kotlin_kit.domain.usecase.DIP20
import com.bity.icp_kotlin_kit.domain.usecase.ICRC1
import com.bity.icp_kotlin_kit.domain.usecase.Tokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.math.BigInteger

class TokensBalanceViewModel(
    private val tokensService: Tokens.TokensService,
): ViewModel() {

    var state: TokensBalanceState by mutableStateOf(TokensBalanceState.TokenWithBalance())
        private set

    fun getTokens(uncompressedPublicKey: String) {
        state = TokensBalanceState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tokens = tokensService.get_all()

                val icrc1Tokens = tokens.filter { token ->
                    val text = token.details.find {
                        it.string == "standard"
                    }?.detail_value as? Tokens.detail_value.Text
                    text?.string?.lowercase() == "icrc1"
                }
                val dip20Tokens = tokens.filter { token ->
                    val text = token.details.find {
                        it.string == "standard"
                    }?.detail_value as? Tokens.detail_value.Text
                    text?.string?.lowercase() == "dip20"
                }
                coroutineScope {
                    val icrc1Deferred = async { getICRC1Tokens(icrc1Tokens, uncompressedPublicKey) }
                    val dip20Deferred = async { getDIP20Balance(dip20Tokens, uncompressedPublicKey) }

                    val result = icrc1Deferred.await() + dip20Deferred.await()
                    state = TokensBalanceState.TokenWithBalance(result)
                }

            } catch (ex: Exception) {
                state = TokensBalanceState.Error(ex.message)
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun getDIP20Balance(
        dip20Tokens: List<Tokens.token>,
        uncompressedPublicKey: String
    ): List<TokenWithBalanceModel> {
        Log.d(TAG, "checking ${dip20Tokens.size} DIP20 tokens")
        return coroutineScope {
            dip20Tokens.map {
                async {
                    val service = DIP20.DIP20Service(
                        canister = it.principal_id
                    )
                    try {
                        val balance = service.balanceOf(
                            who = ICPPrincipal.selfAuthenticatingPrincipal(
                                uncompressedPublicKey.hexToByteArray()
                            )
                        )
                        return@async if(balance != BigInteger.ZERO) {
                            Log.d(TAG, "Balance for ${it.name}: $balance")
                            TokenWithBalanceModel(
                                token = it,
                                balance = balance
                            )
                        } else null
                    } catch (t: Throwable) {
                        Log.e(TAG, "error getting balance for ${it.name}", t)
                        return@async null
                    }
                }
            }
        }.mapNotNull { it.await() }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun getICRC1Tokens(
        icrc1Tokens: List<Tokens.token>,
        uncompressedPublicKey: String
    ): List<TokenWithBalanceModel> {
        Log.d(TAG, "checking ${icrc1Tokens.size} ICRC1 tokens")
        return coroutineScope {
            icrc1Tokens.map {
                async {
                    val service = ICRC1.ICRC1Service(
                        canister = it.principal_id
                    )
                    try {
                        val balance = service.icrc1_balance_of(
                            account = ICRC1.Account(
                                owner = ICPPrincipal.selfAuthenticatingPrincipal(
                                    uncompressedPublicKey = uncompressedPublicKey.hexToByteArray()
                                ),
                                subaccount = null
                            )
                        )
                        if(balance != BigInteger.ZERO) {
                            Log.d(TAG, "Balance for ${it.name}: $balance")
                            return@async TokenWithBalanceModel(
                                token = it,
                                balance = balance
                            )
                        } else return@async null

                    } catch (t: Throwable) {
                        Log.e(TAG, "Unable to fetch balance for ${it.name}", t)
                        return@async null
                    }
                }
            }.mapNotNull { it.await() }
        }
    }

    companion object {
        private const val TAG = "TokensBalanceViewModel"
    }
}