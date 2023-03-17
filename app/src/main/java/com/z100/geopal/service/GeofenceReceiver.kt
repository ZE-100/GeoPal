package com.z100.geopal.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Check for the geofencing transition type
        if (Geofence.GEOFENCE_TRANSITION_ENTER == GeofencingEvent.fromIntent(intent!!)?.geofenceTransition) {
            // Phone entered the geofence, handle the event
            Log.d("YourGeofenceService", "Phone entered the geofence.")
        }
    }
}