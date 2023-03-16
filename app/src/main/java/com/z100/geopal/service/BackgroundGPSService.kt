package com.z100.geopal.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.util.Log.DEBUG
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import com.google.android.gms.location.*
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.pojo.Reminder
import com.z100.geopal.util.Globals
import com.z100.geopal.util.Globals.Factory.NOTIFICATION_CHANNEL_ID

class BackgroundGPSService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onBind(p0: Intent?): IBinder? {
//        updateGPS()
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(NOTIFICATION_CHANNEL_ID, Globals.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT))

        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult) {
               inRangeOfAnyReminder(location.lastLocation)
            }
        }

        val cdt = object : CountDownTimer(1000 * 30, 1) {
            override fun onTick(millisUntilFinished: Long) {

                if ((millisUntilFinished / 1000) % 2 == 0L) {
                    Log.d("TAG", "Tick ${millisUntilFinished / 1000}")
                    inRangeOfAnyReminder(getLastLocation())
                }
            }
            override fun onFinish() {}
        }.start()

        getLastLocation()

        return START_STICKY
    }

    private fun inRangeOfAnyReminder(location: Location?) {
        if (location == null) return

        val reminders: List<Reminder> = ReminderDBHelper(this.applicationContext).findAllReminders()

        reminders.forEach {
            if (it.location!!.lat== location.latitude && it.location.lon == location.longitude) {
                sendNotification(it)
            } else {
                sendNotification("Not working", "Lol")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(): Location? {
        var location: Location? = null
        locationProvider.lastLocation.addOnSuccessListener {
            sendNotification(it.latitude.toString(), it.longitude.toString())
            location = it
        }
        return location
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()

        locationRequest.setInterval(1000 * 30)
        locationRequest.setFastestInterval(5000)
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        return locationRequest
    }

    private fun sendNotification(string: String, s2: String) {
        sendNotification(Reminder(null, string, com.z100.geopal.pojo.Location(s2, -1.0, -1.0), null))
    }

    private fun sendNotification(reminder: Reminder) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(reminder.location?.name ?: "Error")
            .setContentText(reminder.description)
            .setPriority(PRIORITY_DEFAULT)

        notificationManager.notify(0, builder.build())
    }
}