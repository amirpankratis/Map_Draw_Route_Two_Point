package com.example.mapdrawroutetwopoint.utils

class Constants {
    companion object {
        const val BASE_URL = "https://maps.googleapis.com/"
        const val TRAVEL_MODE_DRIVING = "driving"
        const val TRAVEL_MODE_TRANSIT = "transit"
        const val DEFAULT_ZOOM_LAT = 14.621292
        const val DEFAULT_ZOOM_LNG = 121.050288
        const val ZOOM_LEVEL = 11F
        val TRAVEL_MODE_MAP = hashMapOf(
            "Car" to "driving",
            "Public Transportation" to "transit"
        )
    }
}