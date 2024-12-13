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
