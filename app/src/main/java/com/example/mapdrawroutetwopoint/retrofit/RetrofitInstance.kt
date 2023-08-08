package com.example.mapdrawroutetwopoint.retrofit

import com.example.mapdrawroutetwopoint.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val mapApi: MapApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapApi::class.java)
    }
}