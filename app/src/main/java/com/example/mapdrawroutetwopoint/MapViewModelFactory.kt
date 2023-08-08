package com.example.mapdrawroutetwopoint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class MapViewModelFactory(private val mapRepository: MapRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(mapRepository) as T
    }
}