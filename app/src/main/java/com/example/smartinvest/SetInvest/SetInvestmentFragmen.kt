package com.example.smartinvest.SetInvest

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartinvest.R
import com.example.smartinvest.IbAPI.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.smartinvest.FirebaseSingleton




class SetInvestmentFragment : Fragment() {

    private lateinit var searchSymbolEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var stockPriceTextView: TextView
    private lateinit var quantityEditText: EditText
    private lateinit var buyButton: Button
    private lateinit var sellButton: Button

    private val apiService = RetrofitClient.instance
    private val apiKey = "12345-abcde-67890-fghij-12345"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_set_investment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchSymbolEditText = view.findViewById(R.id.searchSymbolEditText)
        searchButton = view.findViewById(R.id.searchButton)
        stockPriceTextView = view.findViewById(R.id.stockPriceTextView)
        quantityEditText = view.findViewById(R.id.quantityEditText)
        buyButton = view.findViewById(R.id.buyButton)
        sellButton = view.findViewById(R.id.sellButton)
        fetchRecentSearches()
        searchButton.setOnClickListener {
            val symbol = searchSymbolEditText.text.toString().trim()
            if (symbol.isNotEmpty()) {
                fetchStockPrice(symbol)
            } else {
                Toast.makeText(context, "Please enter a symboDl", Toast.LENGTH_SHORT).show()
            }
        }

        buyButton.setOnClickListener {
            handleTransaction("BUY")
        }

        sellButton.setOnClickListener {
            handleTransaction("SELL")
        }
    }


    // פונקציה להצגת המחיר ב-TextView או בכל מקום אחר ב-UI
    private fun displayStockPrice(symbol: String, price: Double) {
        val resultTextView = view?.findViewById<TextView>(R.id.stockPriceTextView)
        resultTextView?.text = "Symbol: $symbol\nPrice: $price"
    }


    private fun handleTransaction(action: String) {
        val symbol = searchSymbolEditText.text.toString().trim()
        val quantity = quantityEditText.text.toString().toIntOrNull()

        if (symbol.isEmpty() || quantity == null || quantity <= 0) {
            Toast.makeText(context, "Please enter valid symbol and quantity", Toast.LENGTH_SHORT).show()
            return
        }

        val message = "Are you sure you want to $action $quantity of $symbol?"
        AlertDialog.Builder(context)
            .setTitle("$action Confirmation")
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                performTransaction(action, symbol, quantity)
            }
            .setNegativeButton("No", null)
            .show()
    }


    private fun performTransaction(action: String, symbol: String, quantity: Int) {
        val request = TransactionRequest(symbol, quantity)
        val call = if (action == "BUY") {
            apiService.buyStock(apiKey, request)
        } else {
            apiService.sellStock(apiKey, request)
        }

        call.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Transaction successful", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Transaction failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Toast.makeText(context, "Transaction failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
    private fun fetchStockPrice(symbol: String) {
        val apiService = RetrofitClient.instance
        val apiKey = "12345-abcde-67890-fghij-12345"

        apiService.getStockPrice(apiKey, "price", symbol)
            .enqueue(object : Callback<StockPriceResponse> {
                override fun onResponse(call: Call<StockPriceResponse>, response: Response<StockPriceResponse>) {
                    if (response.isSuccessful) {
                        val stockResponse = response.body()
                        stockResponse?.let {
                            val priceToDisplay = it.ask.takeIf { ask -> ask != null && ask > 0 } ?: it.price
                            displayStockPrice(symbol, priceToDisplay)
                            saveSearchToFirebase(symbol, priceToDisplay)
                        }
                    } else {
                        Toast.makeText(context, "Error fetching stock price", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<StockPriceResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
    val db = FirebaseSingleton.firestoreDatabase
   // private val db = FirebaseFirestore.getInstance()
    // פונקציה לשמירת החיפוש ב-Firebase
    private fun saveSearchToFirebase(symbol: String, price: Double) {
        val search = hashMapOf(
            "symbol" to symbol,
            "price" to price,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("recent_searches")
            .add(search)
            .addOnSuccessListener {
                Toast.makeText(context, "Search saved", Toast.LENGTH_SHORT).show()
                fetchRecentSearches()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to save search: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
    private fun fetchRecentSearches() {
        db.collection("recent_searches")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                val recentSearches = documents.map { doc ->
                    "${doc.getString("symbol")}: ${doc.getDouble("price")}"
                }

                displayRecentSearches(recentSearches)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to load recent searches: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // פונקציה להצגת החיפושים ב-RecyclerView
    private fun displayRecentSearches(searches: List<String>) {
        val recentSearchesTextView = view?.findViewById<TextView>(R.id.recentSearchesTextView)
        recentSearchesTextView?.text = searches.joinToString("\n")
    }


}
