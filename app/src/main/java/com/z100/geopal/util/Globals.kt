package com.z100.geopal.util

import com.z100.geopal.service.data.types.SPType

/**
 * This class is purely there to store global
 * variables and properties, used across the
 * application and as a config-file-replacement
 *
 * @author Z-100
 * @since 1.0
 */
class Globals {
    companion object Factory {
        /* API-properties*/
        const val API_NOMINATIM_REQUEST = "https://nominatim.openstreetmap.org/search.php?format=json&accept-language&limit=3&q="

        /* Notification-properties*/
        const val NOTIFICATION_CHANNEL_ID = "default-channel"
        const val NOTIFICATION_CHANNEL_NAME = "Default Channel"

        /* Geo-properties */
        const val GEOFENCE_RADIUS_IN_METERS = 100.0f
        const val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = 1000 * 60 * 60 * 24

        /* SharedPreferences-properties */
        val SP_SETTINGS_TEST_MODE_TOGGLE = SPType("settings-test-mode-toggle", Boolean::class)
    }
}
