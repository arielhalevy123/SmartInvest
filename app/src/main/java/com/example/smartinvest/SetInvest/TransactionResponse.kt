package com.example.smartinvest.SetInvest

data class TransactionResponse(
    val status: String,
    val symbol: String,
    val quantity: Int,
    val action: String,
    val order_status: String
)
