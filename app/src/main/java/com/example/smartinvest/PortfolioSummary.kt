package com.example.smartinvest

data class PortfolioSummary(
    val user_id: String,
    val total_value: Double,
    val cash_balance: Double,
    val total_profit_loss: Double,
    val total_daily_profit_loss: Double,
    val status: String
)