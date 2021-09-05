package com.example.storelocator.manager.location

import android.location.Location
import com.example.storelocator.model.location.GeoLocation
import kotlinx.coroutines.flow.Flow

interface LocationManager {

    suspend fun getLastKnownPosition(): GeoLocation?

}
