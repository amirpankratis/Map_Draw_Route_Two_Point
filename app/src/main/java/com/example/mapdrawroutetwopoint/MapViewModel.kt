package com.example.mapdrawroutetwopoint

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapdrawroutetwopoint.dto.Route
import com.example.mapdrawroutetwopoint.utils.Constants
import com.example.mapdrawroutetwopoint.utils.TravelModePolylineEnum
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MapViewModel(private val repository: MapRepository) : ViewModel() {
    private val _listOfPolylineOptions: MutableLiveData<List<PolylineOptions>> = MutableLiveData()
    val listOfPolylineOptions: LiveData<List<PolylineOptions>> get() = _listOfPolylineOptions
    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> get() = _error

    var originLocation: LatLng? = null
    var destinationLocation: LatLng? = null
    var currentTravelMode: String = Constants.TRAVEL_MODE_DRIVING

    fun getDirection(origin: String, destination: String, mode: String, key: String) {
        viewModelScope.launch {
            try {
                Log.d("MayData", "getDirection: API CALLED")
                val response = repository.getDirection(origin, destination, mode, key)
                when (response.status) {
                    "OK" -> createPolylineOptions(response.routes, mode)
                    else -> _error.value = "Didn't receive proper directions. Try again."
                }
            } catch (e: IOException) {
                _error.value = "IOException: $e"
            } catch (e: HttpException) {
                _error.value = "HttpException: $e"
            }
        }
    }

    private fun createPolylineOptions(routes: List<Route>, mode: String) {
        // in case transit need to separate  walking polyline from transit itself
        // in case driving need draw differently to get clear curve lines
        when(mode) {
            Constants.TRAVEL_MODE_TRANSIT -> createPolylineOptionsForTransit(routes)
            else -> createPolylineOptionsForDriving(routes)
        }
    }

    private fun createPolylineOptionsForDriving(routes: List<Route>) {
        val polylineOptionsInnerList: MutableList<PolylineOptions> = mutableListOf()
        val decodedPoints : MutableList<LatLng> = mutableListOf()
        routes.forEach { route ->
            route.legs.forEach { leg ->
                leg.steps.forEach { step ->
                    decodedPoints.addAll(PolyUtil.decode(step.polyline.points))
                }
            }
        }
        val polylineOption = PolylineOptions()
            .color(Color.parseColor("#0088FF"))
            .addAll(decodedPoints)
            .geodesic(true)

        polylineOptionsInnerList.add(polylineOption)
        _listOfPolylineOptions.value = polylineOptionsInnerList
    }

    private fun createPolylineOptionsForTransit(routes: List<Route>) {
        val polylineOptionsInnerList: MutableList<PolylineOptions> = mutableListOf()
        routes.forEach { route ->
            route.legs.forEach { leg ->
                leg.steps.forEach { step ->
                    when (step.travel_mode) {
                        TravelModePolylineEnum.WALKING.name -> {
                            val pattern = listOf(
                                Gap(2f), // A transparent space of 20 pixels
                                Dot(), // A small circle with the default radius
                                Gap(2f) // A transparent space of 20 pixels
                            )
                            val polylineOption = PolylineOptions()
                                .addAll(PolyUtil.decode(step.polyline.points))
                                .geodesic(true)
                                .color(Color.parseColor("#0852AC"))
                                .pattern(pattern)
                            polylineOptionsInnerList.add(polylineOption)
                        }
                        else -> {
                            val polylineOption = PolylineOptions()
                                .addAll(PolyUtil.decode(step.polyline.points))
                                .geodesic(true)
                                .color(Color.parseColor("#B15C42"))

                            polylineOptionsInnerList.add(polylineOption)
                        }
                    }
                }
            }
        }
        _listOfPolylineOptions.value = polylineOptionsInnerList
    }
}