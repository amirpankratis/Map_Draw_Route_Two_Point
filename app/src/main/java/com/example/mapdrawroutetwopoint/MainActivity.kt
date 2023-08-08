package com.example.mapdrawroutetwopoint

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mapdrawroutetwopoint.retrofit.RetrofitInstance
import com.example.mapdrawroutetwopoint.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() {

    private var map: GoogleMap? = null
    private lateinit var mapViewModel: MapViewModel
    private var spinner: Spinner? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // init spinner
        travelModeSpinnerSetup()

        // init repository and view model
        val repository = MapRepository(RetrofitInstance.mapApi)
        mapViewModel =
            ViewModelProvider(this, MapViewModelFactory(repository))[MapViewModel::class.java]

        // get mapView from XML and add required lifecycle
        val mapView: MapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        // map view load
        mapView.getMapAsync {
            map = it
            // default zoom to philippine manila
            val zoomLatLng = LatLng(Constants.DEFAULT_ZOOM_LAT, Constants.DEFAULT_ZOOM_LNG)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomLatLng, Constants.ZOOM_LEVEL))

            //if case rotation and ... keep the polyline
            addExistingPolylineToMap()

            // on map click Listener and arrange for 2 click
            it.setOnMapClickListener { selectedLocation ->
                when {
                    mapViewModel.originLocation != null &&
                            mapViewModel.destinationLocation == null -> {
                        // case map 2 tapped clicked
                        mapViewModel.destinationLocation = selectedLocation
                        drawMarkerOnMap()
                        addNewPolylineToMap()
                    }
                    mapViewModel.originLocation == null -> {
                        // case map one tapped clicked
                        mapViewModel.destinationLocation = null
                        mapViewModel.originLocation = selectedLocation
                        drawMarkerOnMap()
                    }
                }
            }
        }

        // get the data on observe from api
        mapViewModel.listOfPolylineOptions.observe(this) {
            drawPolylineOnMap()
        }

        // case any error happens will show without need of loading page
        mapViewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        // spinner of travel type if been clicked will be handled here
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                val selectedItemKey = p0?.getItemAtPosition(p2).toString()
                val selectedItemValue = Constants.TRAVEL_MODE_MAP[selectedItemKey]
                // get value from hashmap if not null get new direction for the mode
                if (selectedItemValue != null && mapViewModel.currentTravelMode != selectedItemValue) {
                    mapViewModel.currentTravelMode = selectedItemValue
                    map?.clear()
                    drawMarkerOnMap()
                    addNewPolylineToMap()

                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // if reset button will be clicked will clear the map
        val button = findViewById<Button>(R.id.resetButton)
        button.setOnClickListener {
            map?.clear()
            mapViewModel.originLocation = null
            mapViewModel.destinationLocation = null
        }

    }

    private fun addNewPolylineToMap() {
        if (
            mapViewModel.originLocation != null &&
            mapViewModel.destinationLocation != null
        ) {
            getDirectionOfLatLng(
                mapViewModel.originLocation!!,
                mapViewModel.destinationLocation!!,
                mapViewModel.currentTravelMode,
            )
        }
    }

    private fun addExistingPolylineToMap() {
        if (
            mapViewModel.originLocation != null &&
            mapViewModel.destinationLocation != null &&
            mapViewModel.listOfPolylineOptions.value?.isNotEmpty() == true
        ) {
            drawPolylineOnMap()
            drawMarkerOnMap()
        }
    }

    private fun drawMarkerOnMap() {
        mapViewModel.originLocation?.let {
            map?.addMarker(MarkerOptions().position(it).title("Origin"))
        }
        mapViewModel.destinationLocation?.let {
            map?.addMarker(MarkerOptions().position(it).title("destination"))
        }
    }

    private fun drawPolylineOnMap() {
        mapViewModel.listOfPolylineOptions.value?.forEach { polylineOption ->
            map?.addPolyline(polylineOption)
        }
    }

//    private fun drawLineOfDirection(polylineOptions: List<PolylineOptions>?) {
//        polylineOptions?.forEach { polylineOption ->
//            map?.addPolyline(polylineOption)
//        }
//    }

    private fun getDirectionOfLatLng(origin: LatLng, destination: LatLng, mode: String) {
        mapViewModel.getDirection(
            "${origin.latitude}, ${origin.longitude}",
            "${destination.latitude}, ${destination.longitude}",
            mode,
            getString(R.string.maps_api_key)
        )
    }

    private fun travelModeSpinnerSetup() {
        spinner = findViewById(R.id.spinnerTravelMode)
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Constants.TRAVEL_MODE_MAP.keys.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        val mapView: MapView = findViewById(R.id.mapView)
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        val mapView: MapView = findViewById(R.id.mapView)
        mapView.onStop()
    }

    override fun onStop() {
        super.onStop()
        val mapView: MapView = findViewById(R.id.mapView)
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        val mapView: MapView = findViewById(R.id.mapView)
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        val mapView: MapView = findViewById(R.id.mapView)
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        val mapView: MapView = findViewById(R.id.mapView)
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapView: MapView = findViewById(R.id.mapView)
        mapView.onSaveInstanceState(outState)
    }

}