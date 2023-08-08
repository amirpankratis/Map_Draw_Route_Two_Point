package com.example.mapdrawroutetwopoint

import com.example.mapdrawroutetwopoint.dto.DirectionDto
import com.example.mapdrawroutetwopoint.retrofit.MapApi

class MapRepository(private val mapApi: MapApi) {
    suspend fun getDirection (origin: String, destination: String, mode: String, key: String): DirectionDto {
        return mapApi.getDirection(origin,destination,mode,key)
    }
}