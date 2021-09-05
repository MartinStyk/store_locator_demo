package com.example.storelocator.model.map

import androidx.annotation.Px
import com.example.storelocator.model.location.GeoLocation

data class MapAreaZoomData(val locations: List<GeoLocation>, @Px val padding: Int)