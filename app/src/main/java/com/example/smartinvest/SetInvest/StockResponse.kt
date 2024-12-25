package com.example.smartinvest.SetInvest

data class StockPriceResponse(
    val symbol: String,
    val price: Double,
    val bid: Double,
    val ask: Double
)
data class PortfolioItem(
    val symbol: String,
    val quantity: Double,
    val purchase_price: Double,
    val current_price: Double,
    val current_value: Double,
    val total_profit_loss: Double,
    val daily_profit_loss: Double,
    val status: String  // הוספת סטטוס השוק (פתוח/סגור)
)
data class FinancialSummaryResponse(
    val cash_balance: Double,
    val net_liquidation: Double,
    val available_funds: Double,
    val total_invested: Double,
    val status: String
)
data class PortfolioSummary(
    val user_id: String,
    val total_value: Double,
    val cash_balance: Double,
    val total_profit_loss: Double,
    val total_daily_profit_loss: Double,
    val status: String
)