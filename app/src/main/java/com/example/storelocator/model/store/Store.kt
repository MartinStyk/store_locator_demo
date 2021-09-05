package com.example.storelocator.model.store

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Store(
    @SerializedName("posId") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("address") val address: String,
    @SerializedName("postalCode") val postalCode: String,
    @SerializedName("city") val city: String,
    @SerializedName("distributorId") val distributorId: Long,
    @SerializedName("country") val country: String,
    @SerializedName("posTypeLogo") val placeLogoUrl: String,
    @SerializedName("productLogo") val productLogoUrl: String,
) : Parcelable