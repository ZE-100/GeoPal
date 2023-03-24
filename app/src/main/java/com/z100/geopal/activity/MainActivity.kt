package com.z100.geopal

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.z100.geopal.R
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.databinding.ActivityMainBinding
import com.z100.geopal.service.api.ApiRequestService
import com.z100.geopal.service.geo.GeoFenceService
import com.z100.geopal.service.data.SPDataService
import com.z100.geopal.ui.fragments.DashboardFragment
import com.z100.geopal.ui.fragments.SettingsFragment


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object Factory {
        lateinit var spDataService: SPDataService
        lateinit var requestQueue: RequestQueue
        lateinit var dbHelper: ReminderDBHelper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        checkAppPermissions()

        if (!appPermissionsGranted())
            this.startActivity(Intent(this, AppPermissionsActivity::class.java))
        else {
            spDataService = SPDataService(getSharedPreferences("preferences", MODE_PRIVATE))
            requestQueue = Volley.newRequestQueue(this)
            dbHelper = ReminderDBHelper(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobInfo: JobInfo = JobInfo.Builder(11, ComponentName(packageName, GeoFenceService::class.java.name)).build()
        jobScheduler.schedule(jobInfo)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupMenuButtons()
    }

    private fun checkAppPermissions() {
        if (checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
            || checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 101
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults.isEmpty() || grantResults[0] == PERMISSION_DENIED) {
                Toast.makeText(this, "Please grant permissions!", Toast.LENGTH_LONG).show()
                throw IllegalArgumentException("Permissions not granted") //TODO
            }
        }
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
