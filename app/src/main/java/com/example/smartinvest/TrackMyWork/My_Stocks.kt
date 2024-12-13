package com.example.smartinvest.TrackMyWork

data class My_Stocks(
    val name: String,
    val currentPrice: Double,
    val purchasePrice: Double,
    val quantity: Int,
    val value: Double,
    val profitLoss: Double,
    val dailyProfitLoss: Double,
    val status: String
)
