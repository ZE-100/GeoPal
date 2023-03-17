package com.z100.geopal.util

class Globals {
    companion object Factory {
        const val SETTINGS_TEST_MODE_TOGGLE = "settings-test-mode-toggle"
        const val API_NOMINATIM_REQUEST = "https://nominatim.openstreetmap.org/search.php?format=json&accept-language&limit=3&q="
        const val NOTIFICATION_CHANNEL_ID = "default-channel"

        const val NOTIFICATION_CHANNEL_NAME = "Default Channel"

        const val GEOFENCE_RADIUS_IN_METERS = 100.0f
        const val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = 1000 * 60 * 60 * 24
    }
}