package com.example.mapdrawroutetwopoint.dto

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
)