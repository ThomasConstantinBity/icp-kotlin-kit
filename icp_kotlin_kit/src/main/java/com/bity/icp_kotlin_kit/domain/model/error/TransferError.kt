package com.bity.icp_kotlin_kit.domain.model.error

sealed class TransferError(errorMessage: String? = null) : Error(errorMessage) {

    class BadFee(expectedFee: ULong) :
        TransferError("Expected fee: $expectedFee")

    class InsufficientFunds(balance: ULong) :
        TransferError("InsufficientFunds - current balance: $balance")

    class TransactionTooOld(allowedWindow: ULong):
        TransferError("TransactionTooOld - allowed window: $allowedWindow")

    class TransactionCreatedInFuture: TransferError()
    class TransactionDuplicate(blockIndex: ULong):
        TransferError("Transaction duplicate of block $blockIndex")
}