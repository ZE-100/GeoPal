import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.z100.geopal.database.helper.ReminderDBHelper
import com.z100.geopal.pojo.Reminder
import com.z100.geopal.service.GeofenceReceiver

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private lateinit var reminders: List<Reminder>

    override fun onReceive(context: Context?, intent: Intent?) {

        reminders = ReminderDBHelper(context!!).findAllReminders()

        Log.d("onReceive()", "Reminders from DB: ${reminders?.get(0)?.description ?: "None"}")
        // Call the method to create the geofence
        reminders.forEach { createGeofence(context, it.location!!.lat, it.location.lon, it.uuid) }
    }

    @SuppressLint("MissingPermission")
    private fun createGeofence(context: Context?, lat: Double, lon: Double, name: String) {
        // Create a GeofencingClient instance
        val geofencingClient = LocationServices.getGeofencingClient(context!!)

        // Get the current location of the phone
        getCurrentPos(context) { location ->
            Log.d("getCurrentPos()", "Current pos: ${location.latitude} : ${location.longitude}")

            // Create a Geofence object
            val geofence = Geofence.Builder()
                .setRequestId(name)
                .setCircularRegion(lat, lon, 1000f) // Radius is set to 1000 meters
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()

            // Create a GeofencingRequest object
            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            // Create a PendingIntent to handle the geofence transitions
            val pendingIntent = Intent(context, GeofenceReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            // Request for geofencing updates
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)?.run {
                addOnSuccessListener {
                    Log.d("YourGeofenceService", "Geofence added successfully.")
                }
                addOnFailureListener {
                    Log.e("YourGeofenceService", "Error adding geofence: ${it.message}")
                }
            }
        }
    }

    private fun getCurrentPos(context: Context, callback: (Location) -> Unit) {
        // Create a FusedLocationProviderClient instance
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request for current location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                // Location found, pass it to the callback function
                location?.let { callback(it) }
            }.addOnFailureListener { e ->
                // Location not found, handle the error
                Log.e("YourGeofenceService", "Error getting location: ${e.message}")
            }
        }
    }
}
