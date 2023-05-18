package com.example.pametnipaketnik

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pametnipaketnik.databinding.ActivityMainBinding
import com.example.pametnipaketnik.startup.StartupPage

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_map_page
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isValueAdded = prefs.contains("username")
        if (!isValueAdded) {
            // Navigate to the startup page if the value is not added
            navController.navigate(R.id.navigation_startuppage)
            // Hide the Bottom Navigation Bar when the startup page is shown
            navView.visibility = View.GONE
        } else {
            // Show the Bottom Navigation Bar when other fragments are shown
            navView.visibility = View.VISIBLE
        }

        // Listen for changes to the selected destination in the Navigation component
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.navigation_startuppage) {
                navView.visibility = View.VISIBLE
            }
        }
    }
}


