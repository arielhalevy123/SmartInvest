package com.example.smartinvest.TrackMyWork

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartinvest.R

class My_Stock_Adapter(private val stocks: List<My_Stocks>) :
    RecyclerView.Adapter<My_Stock_Adapter.StockViewHolder>() {

//    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val stockName: TextView = itemView.findViewById(R.id.stockName)
//        val stockDetails: TextView = itemView.findViewById(R.id.stockDetails)
//
//    }
    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stockName: TextView = itemView.findViewById(R.id.stockName)
        val purchasePrice: TextView = itemView.findViewById(R.id.purchasePrice)
        val currentPrice: TextView = itemView.findViewById(R.id.currentPrice)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val value: TextView = itemView.findViewById(R.id.value)
        val totalProfitLoss: TextView = itemView.findViewById(R.id.totalProfitLoss)
        val dailyProfitLoss: TextView = itemView.findViewById(R.id.dailyProfitLoss)
        val status: TextView = itemView.findViewById(R.id.status)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
//        val stock = stocks[position]
//        holder.stockName.text = stock.name
//        holder.stockDetails.text = """ Purchase Price: ${'$'}${stock.purchasePrice}
//        Current Price: ${'$'}${stock.currentPrice}
//        Quantity: ${stock.quantity}
//        Value: $${stock.value}
//        Total P/L: $${stock.profitLoss}
//        Daily P/L: $${stock.dailyProfitLoss}
//        Status: ${stock.status}
//    """.trimIndent()
        val stock = stocks[position]
        holder.stockName.text = stock.name
        holder.purchasePrice.text = "$${stock.purchasePrice}"
        holder.currentPrice.text = "$${stock.currentPrice}"
        holder.quantity.text = stock.quantity.toString()
        holder.value.text = "$${stock.value}"
        holder.totalProfitLoss.text = "$${stock.profitLoss}"
        holder.dailyProfitLoss.text = "$${stock.dailyProfitLoss}"
        holder.status.text = stock.status


        val backgroundColor = if (position % 2 != 0) {
            holder.itemView.context.getColor(R.color.backgroundColor) // צבע לפוסטים במיקום זוגי
        } else {
            holder.itemView.context.getColor(R.color.white) // צבע לפוסטים במיקום אי-זוגי
        }
        holder.itemView.setBackgroundColor(backgroundColor)
    }

    override fun getItemCount() = stocks.size
}
