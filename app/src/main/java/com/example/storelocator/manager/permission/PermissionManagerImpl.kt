package com.example.storelocator.manager.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.storelocator.dependencyinjection.util.ForApplication
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume

private const val PERMISSIONS_REQUEST_CODE = 777

class PermissionsManagerImpl @Inject constructor(
    @ForApplication private val context: Context,
) : ViewModel(), PermissionManager {

    private var activityWeakReference: WeakReference<Activity>? = null
    private val activity: Activity?
        get() = activityWeakReference?.get()

    private var continuationCallback: CancellableContinuation<PermissionManager.PermissionResult>? = null

    fun bind(activity: AppCompatActivity) {
        activityWeakReference = WeakReference(activity)
    }

    override fun hasPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun shouldShowRationaleForPermission(permission: String): Boolean {
        val activity = activity
        return activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    override suspend fun requestPermissions(permissions: Array<String>): PermissionManager.PermissionResult {
        ActivityCompat.requestPermissions(requireNotNull(activity), permissions, PERMISSIONS_REQUEST_CODE)
        return suspendCancellableCoroutine {
            continuationCallback = it
        }
    }

    override fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray) {
        val granted: MutableList<String> = ArrayList()
        val denied: MutableList<String> = ArrayList()
        for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(permissions[i])
            } else {
                denied.add(permissions[i])
            }
        }
        continuationCallback?.resume(PermissionManager.PermissionResult(granted, denied))
        continuationCallback = null
    }

}
