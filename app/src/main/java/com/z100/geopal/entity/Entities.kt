package com.z100.geopal.entity

import com.z100.geopal.R

data class Reminder(
    val title: String?,
    val description: String,
    val location: Location?,
    val network: Network?
)

data class Location(
    val name: String,
    val lat: Double,
    val lon: Double
) { val drawable: Int = R.drawable.reminder_type_location_icon }

data class Network(
    val name: String,
    val ssid: String
) { val drawable: Int = R.drawable.reminder_type_wifi_icon }