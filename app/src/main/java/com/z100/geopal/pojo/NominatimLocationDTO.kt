package com.z100.geopal.pojo

data class NominatimLocationDTO(
    val place_id: Long,
    val license: String,
    val osm_type: String,
    val osm_id: Long,
    val boundingbox: BoundingBox,
    val lat: String,
    val lon: String,
    val display_name: String,
    val clazz: String,
    val type: String,
    val importance: Double,
    val icon: String
)

data class BoundingBox(
    val values: Array<String>
)

data class NominatimLocation(
    val placeId: Long,
    val displayName: String,
    val lat: Double,
    val lon: Double
)