package com.z100.geopal

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.z100.geopal.databinding.ActivityMainBinding
import com.z100.geopal.service.SPDataService
import com.z100.geopal.ui.fragments.DashboardFragment
import com.z100.geopal.ui.fragments.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object Factory {
        lateinit var spDataService: SPDataService
        lateinit var requestQueue: RequestQueue
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        spDataService = SPDataService(getSharedPreferences("preferences", Context.MODE_PRIVATE))
        requestQueue = Volley.newRequestQueue(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupMenuButtons()
    }

    private fun setupMenuButtons() {

        binding.btnToDashboard.isEnabled = false
        binding.btnToSettings.isEnabled = true

        binding.btnToDashboard.setOnClickListener {
            binding.btnToDashboard.isEnabled = false
            binding.btnToDashboard.setTextAppearance(R.style.button_save)

            binding.btnToSettings.isEnabled = true
            binding.btnToSettings.setTextAppearance(R.style.button_cancel)

            print("Frag to dashboard")

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_content_main, DashboardFragment()) //Container -> R.id.contentFragment
            transaction.commit()
        }

        binding.btnToSettings.setOnClickListener {
            binding.btnToSettings.isEnabled = false
            binding.btnToSettings.setTextAppearance(R.style.button_save)

            binding.btnToDashboard.isEnabled = true
            binding.btnToDashboard.setTextAppearance(R.style.button_cancel)

            print("Frag to settings")

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_content_main, SettingsFragment()) //Container -> R.id.contentFragment
            transaction.commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_content_main)
            .navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}