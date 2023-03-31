package com.z100.geopal.service.geo

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_ENTER
import com.google.android.gms.location.LocationServices
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.pojo.Reminder
import com.z100.geopal.util.Logger.Factory.log

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private lateinit var reminders: List<Reminder>

    override fun onReceive(context: Context?, intent: Intent?) {

        reminders = ReminderDBHelper(context!!).findAllReminders()

        reminders.forEach {
            log(this.javaClass, "Current reminder from DB: {}", it.description)
            createGeofence(context, it.location!!.lat, it.location.lon, it.uuid)
        }
    }

    @SuppressLint("MissingPermission")
    private fun createGeofence(context: Context?, lat: Double, lon: Double, name: String) {
        // Create a GeofencingClient instance
        val geofencingClient = LocationServices.getGeofencingClient(context!!)

        // Get the current location of the phone
        getCurrentPos(context) {
            log(this.javaClass, "Current position: {}/{}", it.latitude.toString(), it.longitude.toString())

            // Create a Geofence object
            val geofence = Geofence.Builder()
                .setRequestId(name)
                .setCircularRegion(lat, lon, 1000f) // Meters
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()

            // Create a GeofencingRequest object
            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            // Create a PendingIntent to handle the geofence transitions
            val pendingIntent = Intent(context, GeofenceReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            // Request for geofencing updates
            geofencingClient.addGeofences(geofencingRequest, pendingIntent).run {
                addOnSuccessListener { log(this.javaClass, "Geofence added successfully") }
                addOnFailureListener { log(this.javaClass, "Error adding geofence: {}", it.message ?: "") }
            }
        }
    }

    private fun getCurrentPos(context: Context, callback: (Location) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // TODO: Rewrite
        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let { callback(it) }
            }.addOnFailureListener {
                log(this.javaClass, "Error getting location: {}", it.message ?: "")
            }
        }
    }
}
