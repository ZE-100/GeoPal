package com.z100.geopal

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
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
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Geofence.*
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.databinding.ActivityMainBinding
import com.z100.geopal.pojo.Reminder
import com.z100.geopal.service.data.SPDataService
import com.z100.geopal.service.geo.GeoFenceService
import com.z100.geopal.service.geo.GeofenceBroadcastReceiver
import com.z100.geopal.ui.fragments.DashboardFragment
import com.z100.geopal.ui.fragments.SettingsFragment
import com.z100.geopal.util.Globals.Factory.APP_PERMISSIONS_NEEDED
import com.z100.geopal.util.Logger.Factory.log
import com.z100.geopal.util.Logger.LogMode.ERROR

class MainActivity : AppCompatActivity() {

    companion object Factory {
        lateinit var spDataService: SPDataService
        lateinit var requestQueue: RequestQueue
        lateinit var dbHelper: ReminderDBHelper

        private const val NOTIFICATION_RESPONSIVENESS = 1000
        private val geofenceList: MutableList<Geofence> by lazy {
            dbHelper.findAllReminders().map { createGeofence(it) }.toMutableList()
        }

        private fun createGeofence(reminder: Reminder): Geofence {
            return Builder().apply {
                setRequestId(reminder.uuid)
                setCircularRegion(
                    reminder.location!!.lat,
                    reminder.location.lon,
                    100f
                )
                setExpirationDuration(NEVER_EXPIRE)
                setLoiteringDelay(GEOFENCE_TRANSITION_DWELL)
                setTransitionTypes(GEOFENCE_TRANSITION_ENTER)
                setNotificationResponsiveness(NOTIFICATION_RESPONSIVENESS)
            }.build()
        }

        fun rescheduleJob(context: Context, packageName: String) {
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val jobInfo: JobInfo = JobInfo.Builder(11, ComponentName(packageName, GeoFenceService::class.java.name)).build()
            jobScheduler.schedule(jobInfo)
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var geofencingClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, FLAG_UPDATE_CURRENT)
    }

    private var btnPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        checkPermissions()
        setupGeofencing()
        setupMainActivity()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_content_main)
            .navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 420) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                checkPermissions()
            } else if (isLocationPermissionPermanentlyDenied()) {
                onPermissionReallyDeniedAlert()
            } else {
                onPermissionDeniedAlert()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val uri = Uri.fromParts("package", packageName, null)

        if (btnPressed && uri != null && uri.toString().startsWith("package:")) {
            btnPressed = false
            checkPermissions()
        }
    }

    private fun setupGeofencing() {
        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    fun addReminderToGeofence(reminder: Reminder) {
        if (reminder.location == null) {
            log(this::class.java, "Reminder:({}) does not have a valid location", reminder.uuid)
            return
        }
        geofenceList.add(createGeofence(reminder))
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
            addOnSuccessListener {
                log(this.javaClass, "Geofence added successfully")
            }
            addOnFailureListener {
                log(ERROR, this.javaClass, "Failed to add geofence: {}", it.message ?: "")
            }
        }
    }

    private fun removeGeofence() {
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                log(this.javaClass, "Geofence removed successfully")
            }
            addOnFailureListener {
                log(ERROR, this.javaClass, "Failed to remove geofence: {}", it.message ?: "")
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

    private fun checkPermissions() {
        if (appHasPermissions()) {
            installSplashScreen()
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
                checkPermissions()
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
                btnPressed = true
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivityForResult(intent, 69)
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

        rescheduleJob(this, packageName)

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
