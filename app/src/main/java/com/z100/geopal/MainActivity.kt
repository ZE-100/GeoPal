package com.z100.geopal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.z100.geopal.databinding.ActivityMainBinding
import com.z100.geopal.service.BackgroundGPSService
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        spDataService = SPDataService(getSharedPreferences("preferences", Context.MODE_PRIVATE))
        requestQueue = Volley.newRequestQueue(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupMenuButtons()
        startService(Intent(this, BackgroundGPSService::class.java))
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_content_main)
            .navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupMenuButtons() {
        binding.btnToDashboard.setOnClickListener {
            setButtonAppearance(binding.btnToDashboard, binding.btnToSettings)

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_content_main, DashboardFragment())
            transaction.commit()
        }

        binding.btnToSettings.setOnClickListener {
            setButtonAppearance(binding.btnToSettings, binding.btnToDashboard)

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_content_main, SettingsFragment())
            transaction.commit()
        }
    }

    private fun setButtonAppearance(btnOne: Button, btnTwo: Button) {
        btnOne.isEnabled = false
        btnOne.setTextAppearance(R.style.button_save)

        btnTwo.isEnabled = true
        btnTwo.setTextAppearance(R.style.button_cancel)
    }
}
