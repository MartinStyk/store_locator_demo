package com.example.storelocator.manager.paysafe.stores

import com.example.storelocator.model.location.GeoLocation
import com.example.storelocator.model.store.Store

private const val SEARCH_RADIUS = 1000
private const val RESULTS = 10
private const val PAGE = 1

interface StoresManager {

    suspend fun loadStoresInPosition(
        geoLocation: GeoLocation,
        radius: Int = SEARCH_RADIUS,
        resultPerPage: Int = RESULTS,
        resultPage: Int = PAGE
    ): List<Store>

}