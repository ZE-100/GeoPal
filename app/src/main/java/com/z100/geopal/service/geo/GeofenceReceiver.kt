package com.z100.geopal.service.geo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER
import com.google.android.gms.location.GeofencingEvent
import com.z100.geopal.util.Logger.Factory.log

/**
 * Class to handle the event of the phone entering
 * a geofence
 *
 * @author Z-100
 * @since 1.0
 */
class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (GeofencingEvent.fromIntent(intent!!)?.geofenceTransition == GEOFENCE_TRANSITION_ENTER) {
            log(this.javaClass, "Phone entered the geofence")
        }
    }
}
