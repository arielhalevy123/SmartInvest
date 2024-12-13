package com.example.smartinvest.TrackMyWork

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.smartinvest.FirebaseConfigHelper
import com.example.smartinvest.SetInvest.PortfolioItem
import com.example.smartinvest.R
import com.example.smartinvest.IbAPI.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackMyWorkFragment : Fragment() {

    private lateinit var progressBar: View  // To manage the ProgressBar
    private lateinit var recyclerView: RecyclerView  // To manage the RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ניפוח קובץ ה-XML
        println("TrackMyWorkFragment - onCreateView called")
        val view = inflater.inflate(R.layout.fragment_track_my_work, container, false)

        // Initialize the ProgressBar and RecyclerView
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.stocksRecyclerView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("TrackMyWorkFragment - onViewCreated is called")

        progressBar.visibility = View.VISIBLE


        // משיכת מפתח ה-API מ-Firebase Remote Config
        FirebaseConfigHelper.fetchApiKey { apiKey ->
            if (apiKey != null) {
                println("API Key fetched successfully: $apiKey")

                val apiService = RetrofitClient.instance

                apiService.getPortfolio(apiKey, "portfolio")
                    .enqueue(object : Callback<List<PortfolioItem>> {
                        override fun onResponse(
                            call: Call<List<PortfolioItem>>,
                            response: Response<List<PortfolioItem>>
                        ) {
                            progressBar.visibility = View.GONE  // הסתרת ה-ProgressBar

                            if (response.isSuccessful) {
                                val portfolio = response.body()
                                println("Portfolio fetched successfully: $portfolio")

                                portfolio?.let {
                                    val adapter = My_Stock_Adapter(it.map { item ->
                                        My_Stocks(
                                            name = item.symbol,
                                            currentPrice = item.current_price,
                                            purchasePrice = item.purchase_price,
                                            quantity = item.quantity.toInt(),
                                            value = item.current_value,
                                            profitLoss = item.total_profit_loss,
                                            dailyProfitLoss = item.daily_profit_loss,
                                            status = item.status
                                        )
                                    })
                                    recyclerView.adapter = adapter
                                    println("RecyclerView updated with portfolio data")
                                }
                            } else {
                                println(
                                    "Error fetching portfolio: ${
                                        response.errorBody()?.string()
                                    }"
                                )
                            }
                        }

                        override fun onFailure(call: Call<List<PortfolioItem>>, t: Throwable) {
                            progressBar.visibility = View.GONE
                            println("Failed to fetch portfolio: ${t.message}")
                        }
                    })

            } else {
                progressBar.visibility = View.GONE
                println("Failed to fetch API key from Firebase Remote Config")
            }
        }
    }

}
