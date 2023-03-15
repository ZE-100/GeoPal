package com.z100.geopal.entity

data class Reminder(
    val title: String?,
    val description: String,
    val location: Location?,
    val network: Network?
)

data class Location(
    val name: String,
    val lat: Double,
    val long: Double
)

data class Network(
    val name: String,
    val ssid: String
)