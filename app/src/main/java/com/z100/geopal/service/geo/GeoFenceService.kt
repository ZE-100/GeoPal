package com.z100.geopal.service.geo

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Handler
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.pojo.Reminder
import com.z100.geopal.util.Globals.Factory.NOTIFICATION_CHANNEL_ID
import com.z100.geopal.util.Logger.Factory.log
import com.z100.geopal.util.Logger.LogMode.ERROR
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import kotlin.coroutines.resumeWithException

class GeoFenceService : JobService() {

    private val context: Context = this
    private var firstExec = true

    private lateinit var notificationManager: NotificationManager
    private var geofenceList: MutableList<Geofence> = mutableListOf()
    private var reminders: List<Reminder> = mutableListOf()

    private var handler = Handler()
    private lateinit var runnable: Runnable

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStartJob(params: JobParameters?): Boolean {

        runnable = Runnable {}

        log(this.javaClass, "GPS-Service started")

        setupNotificationManager()

        if (firstExec) {
            reminders = ReminderDBHelper(context).findAllReminders()

            if (reminders.isEmpty()) return true

            reminders.forEach { createGeofences(it) }
            startGeofencing()
            firstExec = false
        }

        runnable = Runnable {
            GlobalScope.launch {
                val currentLocation = getCurrentPosition()
                inRangeOfGeoFence(currentLocation)
            }
            handler.postDelayed(runnable, 1000 * 5)
        }

        handler.postDelayed(runnable, 1000 * 5)
        return true
    }

    private fun createGeofences(reminder: Reminder) {
        reminder.uuid = UUID.randomUUID().toString()
        val geofence = Geofence.Builder().apply {
            setRequestId(reminder.uuid)
            setCircularRegion(
                reminder.location!!.lat,
                reminder.location.lon,
                1000f
            ) // Radius is set to 1000 meters
            setExpirationDuration(Geofence.NEVER_EXPIRE)
            setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
        }.build()
        geofenceList.add(geofence)
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        handler.removeCallbacks(runnable)
        log(this.javaClass, "GPS-Service destroyed")
        return true
    }

    @SuppressLint("MissingPermission")
    private fun startGeofencing() {
        val geofencingClient = LocationServices.getGeofencingClient(context)

        val geofencePendingIntent: PendingIntent by lazy {
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        }

        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
            addOnSuccessListener {
                log(this.javaClass, "Geofence added successfully")
            }
            addOnFailureListener {
                log(ERROR, this.javaClass, "Failed to add geofence: {}", it.message ?: "")
            }
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentPosition(): Location {
        return withContext(Dispatchers.IO) {
            val locationTask = LocationServices.getFusedLocationProviderClient(context).lastLocation
            suspendCancellableCoroutine { continuation ->
                locationTask.addOnSuccessListener {
                    if (it != null) {
                        continuation.resume(it) {
                            log(this.javaClass, "Location search cancelled")
                        }
                    } else {
                        continuation.resume(Location("")) {
                            log(this.javaClass, "Unable to get location")
                        }
                    }
                }.addOnFailureListener { continuation.resumeWithException(it) }

                continuation.invokeOnCancellation {}
            }
        }
    }

    private fun inRangeOfGeoFence(currentLocation: Location): Boolean {
        log(this.javaClass, "Start Geofence matching")

        geofenceList.forEach {
            val geofenceLocation = Location("").apply {
                latitude = it.latitude
                longitude = it.longitude
            }

            val uuid = it.requestId

            log(this.javaClass, "Currently checking location: {}", geofenceLocation.toString())

            val distance = currentLocation.distanceTo(geofenceLocation)
            if (distance <= it.radius) {
                log(this.javaClass, "Inside of GeoLocation: {}", distance.toString())
                val reminder = reminders.firstOrNull { reminder -> reminder.uuid == uuid }
                sendNotification(reminder ?: createErrorEntry())
                reminder?.alreadyReminded = true
                return true
            } else {
                log(this.javaClass, "Not inside of GeoLocation: {}", distance.toString())
            }
        }
        return false
    }

    private fun setupNotificationManager() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Background Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun sendNotification(reminder: Reminder) {
        if (reminder.alreadyReminded)
            return
        sendNotification(reminder.description, reminder.location?.name ?: "Error")
    }

    private fun sendNotification(title: String, description: String) {
        val notification: Notification = NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID).apply {
                setSmallIcon(com.z100.geopal.R.drawable.app_icon)
                setContentTitle(title)
                setContentText(description)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }.build()

        log(this.javaClass, "Create notification with title: {}", title)

        notificationManager.notify(123, notification)
    }

    private fun createErrorEntry(): Reminder {
        return Reminder(null, "Something went wrong", null, null)
    }
}
