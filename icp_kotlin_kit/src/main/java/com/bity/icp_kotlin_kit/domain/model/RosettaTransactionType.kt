package com.bity.icp_kotlin_kit.domain.model

sealed class RosettaTransactionType(val from : String?, val to: String?) {
    class Mint(to: String): RosettaTransactionType(null, to)
    class Burn(from: String): RosettaTransactionType(from, null)
    class Send(from: String, to: String): RosettaTransactionType(from, to)
}