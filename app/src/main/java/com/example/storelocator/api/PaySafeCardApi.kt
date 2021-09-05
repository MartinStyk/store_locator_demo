package com.example.storelocator.api

import com.example.storelocator.model.store.Stores
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface PaySafeCardApi {
    companion object {
        const val URL = "https://rest.paysafecard.com/rest/"
        const val CERTIFICATE_PINS = "pins.json"
    }

    @GET("stores")
    suspend fun stores(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Int,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
    ): Stores
}