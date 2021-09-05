package com.example.storelocator.model.store

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Stores(
    @SerializedName("stores") val stores: List<Store>,
) : Parcelable