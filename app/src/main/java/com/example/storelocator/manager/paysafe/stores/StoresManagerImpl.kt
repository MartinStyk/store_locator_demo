package com.example.storelocator.manager.paysafe.stores

import com.example.storelocator.api.PaySafeCardApi
import com.example.storelocator.model.location.GeoLocation
import com.example.storelocator.model.store.Store
import javax.inject.Inject

class StoresManagerImpl @Inject constructor(
    private val paySafeCardApi: PaySafeCardApi
) : StoresManager {

    override suspend fun loadStoresInPosition(
        geoLocation: GeoLocation,
        radius: Int,
        resultPerPage: Int,
        resultPage: Int
    ): List<Store> {

        return paySafeCardApi.stores(
            geoLocation.latitude,
            geoLocation.longitude,
            radius,
            resultPerPage,
            resultPage
        ).stores
    }
}