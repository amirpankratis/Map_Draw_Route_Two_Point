package com.example.mapdrawroutetwopoint.retrofit

import com.example.mapdrawroutetwopoint.dto.DirectionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApi {

    @GET("maps/api/directions/json")
    suspend fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") key: String
    ): DirectionDto
}