package com.example.smartinvest

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class AIFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_ai, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchButton = view.findViewById(R.id.searchButton)
        resultTextView = view.findViewById(R.id.resultTextView)

        searchEditText.filters = arrayOf(InputFilter.LengthFilter(4))

        searchButton.setOnClickListener {
            val stockName = searchEditText.text.toString().trim()
            if (stockName.isNotEmpty()) {
                if (stockName.length <= 4) {
                    fetchApiKeyAndRecommend(stockName)
                } else {
                    resultTextView.text = "Please enter 4 characters or less."
                }
            } else {
                resultTextView.text = "Please enter a stock name."
            }
        }

        return view
    }

    // Fetch API key and then call the function to connect to OpenAI
    private fun fetchApiKeyAndRecommend(stockName: String) {
        FirebaseConfigHelper.fetchOpenAiApiKey { apiKey ->
            if (apiKey != null) {
                Log.d("FirebaseConfig", "API Key: $apiKey")
                fetchRecommendation(stockName, apiKey)
            } else {
                resultTextView.text = "Error: Failed to load the API key."
                Log.e("FirebaseConfig", "Failed to fetch API Key")
            }
        }
    }

    // Connect to OpenAI API and fetch recommendation
    private fun fetchRecommendation(stockName: String,apiKey: String) {


            Log.d("FirebaseConfig", "API Key: $apiKey")

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(OpenAIApiService::class.java)

            val prompt = "Should I buy the stock $stockName? Provide a recommendation and the stock symboll (buy/sell) and a 10-word reason."
            val request = OpenAIRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(mapOf("role" to "user", "content" to prompt)),
                max_tokens = 50
            )

            val call = service.getRecommendation("Bearer $apiKey", request)
            call.enqueue(object : Callback<OpenAIResponse> {
                override fun onResponse(call: Call<OpenAIResponse>, response: Response<OpenAIResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val recommendation = result?.choices?.get(0)?.message?.content ?: "No response received"
                        resultTextView.text = recommendation
                    } else {
                        resultTextView.text = "Request error: ${response.errorBody()?.string()}"
                    }
                }

                override fun onFailure(call: Call<OpenAIResponse>, t: Throwable) {
                    resultTextView.text = "Error: ${t.message}"
                }
            })



    }

}
