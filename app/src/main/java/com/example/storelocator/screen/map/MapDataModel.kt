package com.example.storelocator.screen.map

import androidx.lifecycle.ViewModel
import com.example.storelocator.model.location.GeoLocation
import com.example.storelocator.model.map.MapAreaZoomData
import com.example.storelocator.model.map.MapLocationZoomData
import com.example.storelocator.model.map.MapPinData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class MapDataModel @Inject constructor() : ViewModel() {

    private val showMyPositionFlow = MutableSharedFlow<Boolean>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val showMyPosition: Flow<Boolean> = showMyPositionFlow

    private val showPinsFlow = MutableSharedFlow<List<MapPinData>>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val showPins: Flow<List<MapPinData>> = showPinsFlow

    private val zoomOnAreaFlow = MutableSharedFlow<MapAreaZoomData>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val zoomOnArea: Flow<MapAreaZoomData> = zoomOnAreaFlow

    private val zoomOnLocationFlow = MutableSharedFlow<MapLocationZoomData>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val zoomOnLocation: Flow<MapLocationZoomData> = zoomOnLocationFlow

    private val markerClickFlow = MutableSharedFlow<GeoLocation>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val markerClick: Flow<GeoLocation> = markerClickFlow

    fun enableMyPosition(enable: Boolean) {
        showMyPositionFlow.tryEmit(enable)
    }

    fun showPins(pins: List<MapPinData>) {
        showPinsFlow.tryEmit(pins)
    }

    fun zoomOnArea(data: MapAreaZoomData) {
        zoomOnAreaFlow.tryEmit(data)
    }

    fun zoomOnLocation(data: MapLocationZoomData) {
        zoomOnLocationFlow.tryEmit(data)
    }

    fun markerClicked(location: GeoLocation) {
        markerClickFlow.tryEmit(location)
    }

}

