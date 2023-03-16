package com.z100.geopal.pojo

data class NominatimLocationDTO(
    val placeId: Long,
    val displayName: String,
    val lat: Long,
    val lon: Long
)