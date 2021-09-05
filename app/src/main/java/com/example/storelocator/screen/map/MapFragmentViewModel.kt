package com.example.storelocator.screen.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storelocator.model.location.GeoLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class MapFragmentViewModel @AssistedInject constructor(
    @Assisted private val mapDataModel: MapDataModel,
) : ViewModel(), GoogleMap.OnMarkerClickListener {

    val showMyPosition = mapDataModel.showMyPosition.asLiveData()

    val showPins = mapDataModel.showPins.asLiveData()

    val cameraAnimation = flowOf(
        mapDataModel.zoomOnArea.map {
            val bounds = with(LatLngBounds.builder()) {
                it.locations.forEach { include(LatLng(it.latitude, it.longitude)) }
                build()
            }
            CameraUpdateFactory.newLatLngBounds(bounds, it.padding)
        },
        mapDataModel.zoomOnLocation.map {
            CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude))
        })
        .flattenMerge()
        .asLiveData()

    override fun onMarkerClick(marker: Marker): Boolean {
        mapDataModel.markerClicked(GeoLocation(marker.position.latitude, marker.position.longitude))
        return true
    }

    @AssistedFactory
    interface Factory {
        fun create(mapDataModel: MapDataModel): MapFragmentViewModel
    }

}