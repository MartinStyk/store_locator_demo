package com.example.storelocator.manager.permission

interface PermissionManager {

    data class PermissionResult(
            val grantedPermissions: List<String>,
            val deniedPermissions: List<String>
    )

    fun hasPermissionGranted(permission: String): Boolean
    fun shouldShowRationaleForPermission(permission: String): Boolean

    suspend fun requestPermissions(permissions: Array<String>): PermissionResult

    fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray)

}