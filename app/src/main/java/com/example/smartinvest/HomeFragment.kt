package com.example.smartinvest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartinvest.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        // הגדרת הגרפים
        setupSectorPieChart()
        setupAssetBarChart()

        // הגדרת ה-RecyclerViews לביצועים ולדיבידנדים
        binding.stockPerformanceRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.dividendsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSectorPieChart() {
        val entries = listOf(
            PieEntry(40f, "Technology"),
            PieEntry(20f, "Healthcare"),
            PieEntry(15f, "Finance"),
            PieEntry(25f, "Energy")
        )

        val dataSet = PieDataSet(entries, "Sectors")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val pieData = PieData(dataSet)
        binding.sectorPieChart.data = pieData
        binding.sectorPieChart.invalidate() // רענון הגרף
    }

    private fun setupAssetBarChart() {
        val entries = listOf(
            BarEntry(0f, 50f), // מניות
            BarEntry(1f, 20f), // אג"ח
            BarEntry(2f, 15f), // מזומן
            BarEntry(3f, 15f)  // קרנות
        )

        val dataSet = BarDataSet(entries, "Assets")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val barData = BarData(dataSet)
        binding.assetBarChart.data = barData
        binding.assetBarChart.invalidate() // רענון הגרף
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
