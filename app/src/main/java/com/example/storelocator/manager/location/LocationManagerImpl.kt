package com.example.storelocator.manager.location

import android.annotation.SuppressLint
import android.location.Location
import com.example.storelocator.model.location.GeoLocation
import com.example.storelocator.util.coroutine.AppCoroutineScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@SuppressLint("MissingPermission")
class LocationManagerImpl @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : LocationManager {

    override suspend fun getLastKnownPosition(): GeoLocation? {
        return fusedLocationProviderClient.lastLocation.await()?.let {
            GeoLocation(it.latitude, it.longitude)
        }
    }

}