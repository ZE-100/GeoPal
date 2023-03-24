package com.z100.geopal

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.databinding.ActivityMainBinding
import com.z100.geopal.service.data.SPDataService
import com.z100.geopal.service.geo.GeoFenceService
import com.z100.geopal.ui.fragments.DashboardFragment
import com.z100.geopal.ui.fragments.SettingsFragment
import com.z100.geopal.util.Globals.Factory.APP_PERMISSIONS_NEEDED

class MainActivity : AppCompatActivity() {

    companion object Factory {
        lateinit var spDataService: SPDataService
        lateinit var requestQueue: RequestQueue
        lateinit var dbHelper: ReminderDBHelper
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var permissionReallyDenied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        checkPermissionsAndSetup()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_content_main)
            .navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 420) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                checkPermissionsAndSetup()
            } else if (isLocationPermissionPermanentlyDenied()) {
                onPermissionReallyDeniedAlert()
            } else {
                onPermissionDeniedAlert()
            }
        }
    }

    private fun isLocationPermissionPermanentlyDenied(): Boolean {
        var permanentlyDenied = false

        APP_PERMISSIONS_NEEDED.forEach {
            permanentlyDenied = (checkSelfPermission(this, it) == PERMISSION_DENIED
                    && !shouldShowRequestPermissionRationale(this, it))
        }

        return permanentlyDenied
    }
    private fun checkPermissionsAndSetup() {
        if (appHasPermissions()) {
            installSplashScreen()
            setupMainActivity()
        } else {
            ActivityCompat.requestPermissions(this, APP_PERMISSIONS_NEEDED, 420)
        }
    }

    private fun appHasPermissions(): Boolean {
        APP_PERMISSIONS_NEEDED.forEach {
            if (checkSelfPermission(this, it) == PERMISSION_DENIED)
                return false
        }
        return true
    }

    private fun onPermissionDeniedAlert() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.app_icon_high)
            .setTitle(R.string.permission_denied_title)
            .setMessage(R.string.permission_denied_message)
            .setPositiveButton("Allow") { _, _ ->
                checkPermissionsAndSetup()
            }
            .setCancelable(false)
            .create().show()
    }

    private fun onPermissionReallyDeniedAlert() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.app_icon_high)
            .setTitle(R.string.permission_really_denied_title)
            .setMessage(R.string.permission_really_denied_message)
            .setPositiveButton("Go to settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivityForResult(intent, 420)
            }
            .setCancelable(false)
            .create().show()
    }

    private fun setupMainActivity() {
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
