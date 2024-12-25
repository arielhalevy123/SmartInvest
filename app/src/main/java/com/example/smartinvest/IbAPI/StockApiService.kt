package com.example.smartinvest.IbAPI
import com.example.smartinvest.SetInvest.FinancialSummaryResponse
import com.example.smartinvest.SetInvest.PortfolioItem
import com.example.smartinvest.SetInvest.PortfolioSummary
import com.example.smartinvest.SetInvest.StockPriceResponse
import com.example.smartinvest.SetInvest.TransactionRequest
import com.example.smartinvest.SetInvest.TransactionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface StockApiService {
    @GET("/get_stock_info")
    fun getStockPrice(
        @Header("x-api-key") apiKey: String,
        @Query("type") type: String,
        @Query("symbol") symbol: String
    ): Call<StockPriceResponse>

    @GET("/get_stock_info")
    fun getPortfolio(
        @Header("x-api-key") apiKey: String,
        @Query("type") type: String
    ): Call<List<PortfolioItem>>

    @GET("/get_stock_info")
    fun getSummary(
        @Header("x-api-key") apiKey: String,
        @Query("type") type: String
    ): Call<PortfolioSummary>

    @POST("/buy_stock")
    fun buyStock(
        @Header("x-api-key") apiKey: String,
        @Body body: TransactionRequest
    ): Call<TransactionResponse>

    @POST("/sell_stock")
    fun sellStock(
        @Header("x-api-key") apiKey: String,
        @Body body: TransactionRequest
    ): Call<TransactionResponse>

    @GET("/get_financial_summary")
    fun getFinancialSummary(
        @Header("x-api-key") apiKey: String
    ): Call<FinancialSummaryResponse>

}
