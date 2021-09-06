package com.example.storelocator.manager.location

const val GOOGLE_API_CLIENT_REQUEST_CODE = 4321

interface LocationRequestManager {

    val isGpsEnabled: Boolean

    suspend fun requestGpsEnable(): Boolean

    fun onActivityResult(requestCode: Int, resultCode: Int)

}