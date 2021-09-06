package com.example.storelocator.model.map

import com.example.storelocator.model.location.GeoLocation

data class MapLocationZoomData(val location: GeoLocation, val zoom: Float? = null)