package com.bity.icp_kotlin_kit.domain.model


sealed class ICPTransactionType(val from : String?, val to: String?) {
    class Mint(to: String): ICPTransactionType(null, to)
    class Burn(from: String): ICPTransactionType(from, null)
    class Send(from: String, to: String): ICPTransactionType(from, to)
}