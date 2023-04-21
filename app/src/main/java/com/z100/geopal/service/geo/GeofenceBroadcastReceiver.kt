package com.z100.geopal.service.geo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.GeofenceStatusCodes.getStatusCodeString
import com.google.android.gms.location.GeofencingEvent
import com.z100.geopal.pojo.Reminder
import com.z100.geopal.util.Globals.Factory.BACKGROUND_CHANNEL_ID
import com.z100.geopal.util.Globals.Factory.BACKGROUND_CHANNEL_NAME
import com.z100.geopal.util.Globals.Factory.NOTIFICATION_CHANNEL_ID
import com.z100.geopal.util.Logger.Factory.log
import com.z100.geopal.util.Logger.LogMode.ERROR

/**
 * Handles the event of the phone entering
 * any of the provided Geofences - in this
 * case sending a notification.
 *
 * @author Z-100
 * @since 2.0
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

    private lateinit var reminders: List<Reminder>

    override fun onReceive(context: Context, intent: Intent?) {

        this.context = context
        this.notificationManager = setupNotificationManager()

        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) }

        if (geofencingEvent?.hasError() == true) {
            val errorMessage = getStatusCodeString(geofencingEvent.errorCode)
            log(ERROR, this.javaClass, "Failed to retrieve Geofence: {}", errorMessage)
            return
        }

        if (geofencingEvent!!.geofenceTransition != GEOFENCE_TRANSITION_ENTER)
            log(this.javaClass, "Geofence:({}) didn't enter: {}")

        geofencingEvent.triggeringGeofences?.forEach {
            reminders.forEach { that -> if (it.requestId == that.uuid) sendNotification(that) }
        } ?: log(ERROR, this.javaClass, "Triggered Geofences null")
    }

    private fun setupNotificationManager(): NotificationManager {
        val notificationManager = getSystemService(context, NotificationManager::class.java) as NotificationManager

        val notificationChannel = NotificationChannel(BACKGROUND_CHANNEL_ID, BACKGROUND_CHANNEL_NAME, IMPORTANCE_DEFAULT)

        notificationManager.createNotificationChannel(notificationChannel)

        return notificationManager
    }

    private fun sendNotification(reminder: Reminder) {
        if (reminder.alreadyReminded) return
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

        log(this.javaClass, "Created notification with title: {}", title)

        notificationManager.notify(123, notification)
    }
}
