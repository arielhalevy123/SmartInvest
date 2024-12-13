package com.example.smartinvest

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.smartinvest.IbAPI.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private lateinit var userIdTextView: TextView
private lateinit var valueTextView: TextView
private lateinit var dailyPnLTextView: TextView

class InvestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invest)
        // Initialize TextViews
        userIdTextView = findViewById(R.id.userIdTextView)
        valueTextView = findViewById(R.id.valueTextView)
        dailyPnLTextView = findViewById(R.id.dailyPnLTextView)

        // Fetch and display user data
        fetchUserData()

        // Get NavHostFragment and NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Find BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Connect the BottomNavigationView with NavController
        bottomNav.setupWithNavController(navController)

        // Optional: Handle destination changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
        }
    }

    private fun fetchUserData() {
        val apiService = RetrofitClient.instance
        val apiKey = "12345-abcde-67890-fghij-12345"

        // Fetch portfolio summary
        apiService.getSummary(apiKey, "summary").enqueue(object : Callback<PortfolioSummary> {
            override fun onResponse(call: Call<PortfolioSummary>, response: Response<PortfolioSummary>) {
                if (response.isSuccessful) {
                    val summary = response.body()
                    summary?.let {
                        userIdTextView.text = "User ID: ${it.user_id}"
                        valueTextView.text = "Total Value: %.2f".format(it.total_value)
                        dailyPnLTextView.text = "Daily P&L: %.2f".format(it.total_daily_profit_loss)
                    }
                } else {
                    userIdTextView.text = "Error fetching data"
                }
            }

            override fun onFailure(call: Call<PortfolioSummary>, t: Throwable) {
                userIdTextView.text = "Error fetching data: ${t.message}"
            }
        })
    }


}
