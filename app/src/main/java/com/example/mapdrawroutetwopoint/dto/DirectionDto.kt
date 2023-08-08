package com.example.mapdrawroutetwopoint.dto

data class DirectionDto(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)