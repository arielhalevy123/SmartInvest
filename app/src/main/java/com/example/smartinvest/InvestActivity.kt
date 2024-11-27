package com.example.smartinvest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class InvestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invest)

        // Get NavHostFragment and NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Find BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Connect the BottomNavigationView with NavController
        bottomNav.setupWithNavController(navController)

        // Optional: If you want to handle destination changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Handle destination changes (e.g., update title)
            supportActionBar?.title = destination.label
        }
    }
}
