package com.example.smartinvest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartinvest.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.example.smartinvest.FirebaseConfigHelper
import com.example.smartinvest.IbAPI.RetrofitClient
import com.example.smartinvest.SetInvest.PortfolioItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import com.example.smartinvest.SetInvest.FinancialSummaryResponse

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // משתנים לשמירת הנתונים בזיכרון
    private var portfolio: List<PortfolioItem> = emptyList()
    private var financialSummary: FinancialSummaryResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (portfolio.isNotEmpty() && financialSummary != null) {
            setupSectorPieChart(portfolio, financialSummary!!.cash_balance)
            displayDetails()
        } else {
            binding.pieChartProgressBar.visibility = View.VISIBLE
            binding.sectorPieChart.visibility = View.GONE
        }


        // קריאת הנתונים ועדכון ה-UI
        fetchPortfolioAndSetupPieChart()
        fetchPortfolioAndSetupDetails()
    }

    private fun fetchPortfolioAndSetupPieChart() {
        binding.pieChartProgressBar.visibility = View.VISIBLE // הצגת ה-ProgressBar
        binding.sectorPieChart.visibility = View.GONE // הסתרת ה-PieChart

        FirebaseConfigHelper.fetchApiKey { apiKey ->
            if (apiKey != null) {
                val apiService = RetrofitClient.instance

                apiService.getPortfolio(apiKey, "portfolio").enqueue(object : Callback<List<PortfolioItem>> {
                    override fun onResponse(call: Call<List<PortfolioItem>>, response: Response<List<PortfolioItem>>) {
                        if (response.isSuccessful) {
                            portfolio = response.body() ?: emptyList()

                            apiService.getFinancialSummary(apiKey).enqueue(object : Callback<FinancialSummaryResponse> {
                                override fun onResponse(call: Call<FinancialSummaryResponse>, response: Response<FinancialSummaryResponse>) {
                                    binding.pieChartProgressBar.visibility = View.GONE // הסתרת ה-ProgressBar
                                    binding.sectorPieChart.visibility = View.VISIBLE // הצגת ה-PieChart

                                    if (response.isSuccessful) {
                                        financialSummary = response.body()
                                        financialSummary?.let { summary ->
                                            setupSectorPieChart(portfolio, summary.cash_balance)
                                        }
                                    } else {
                                        Toast.makeText(context, "Failed to fetch financial summary", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<FinancialSummaryResponse>, t: Throwable) {
                                    binding.pieChartProgressBar.visibility = View.GONE
                                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {
                            binding.pieChartProgressBar.visibility = View.GONE
                            Toast.makeText(context, "Failed to fetch portfolio", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<List<PortfolioItem>>, t: Throwable) {
                        binding.pieChartProgressBar.visibility = View.GONE
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                binding.pieChartProgressBar.visibility = View.GONE
                Toast.makeText(context, "Failed to fetch API key from Firebase", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupSectorPieChart(portfolio: List<PortfolioItem>, cashBalance: Double) {
        val entries = portfolio.map { item ->
            PieEntry(item.current_value.toFloat(), item.symbol)
        }.toMutableList()

        if (cashBalance > 0) {
            entries.add(PieEntry(cashBalance.toFloat(), "Cash"))
        }

        val dataSet = PieDataSet(entries, "Sectors")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val pieData = PieData(dataSet)
        binding.sectorPieChart.data = pieData
        binding.sectorPieChart.invalidate()
    }


    private fun fetchPortfolioAndSetupDetails() {
        if (financialSummary == null || portfolio.isEmpty()) {
            Toast.makeText(context, "Data not loaded yet", Toast.LENGTH_SHORT).show()
            return
        }

        displayDetails()
    }


    private fun displayDetails() {
        val detailsContainer = binding.detailsContainer
        detailsContainer.removeAllViews() // ניקוי נתונים קיימים

        // נתוני הסיכום הפיננסי
        financialSummary?.let { summary ->
            val summaryData = listOf(
                "Cash Balance: ${summary.cash_balance}",
                "Net Liquidation: ${summary.net_liquidation}",
                "Available Funds: ${summary.available_funds}",
                "Total Invested: ${summary.total_invested}",
                "Status: ${summary.status}"
            )

            for (item in summaryData) {
                detailsContainer.addView(createCardView(item))
            }
        }

        // נתוני הפורטפוליו
        for (portfolioItem in portfolio) {
            val portfolioText = "Symbol: ${portfolioItem.symbol}\n" +
                    "Quantity: ${portfolioItem.quantity}\n" +
                    "Purchase Price: ${portfolioItem.purchase_price}\n" +
                    "Current Price: ${portfolioItem.current_price}\n" +
                    "Current Value: ${portfolioItem.current_value}\n" +
                    "Profit/Loss: ${portfolioItem.total_profit_loss}\n" +
                    "Daily P&L: ${portfolioItem.daily_profit_loss}"
            detailsContainer.addView(createCardView(portfolioText))
        }
    }

    private fun createCardView(text: String): View {
        val context = requireContext()

        val cardView = androidx.cardview.widget.CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 8, 16, 8)
            }
            radius = 12f
            cardElevation = 6f
        }

        val textView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
            this.text = text
            textSize = 16f
        }

        cardView.addView(textView)
        return cardView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
