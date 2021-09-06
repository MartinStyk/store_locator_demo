package com.example.storelocator.manager.location

import android.app.Activity
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.coroutines.resume
import android.location.LocationManager as AndroidLocationManager

class LocationRequestManagerImpl @Inject constructor(private val locationManager: AndroidLocationManager) : ViewModel(), LocationRequestManager {

    private var activityWeakReference: WeakReference<Activity>? = null
    private val activity: Activity?
        get() = activityWeakReference?.get()

    fun bind(activity: Activity) {
        activityWeakReference = WeakReference(activity)
    }

    private var continuationCallback: CancellableContinuation<Boolean>? = null

    override val isGpsEnabled: Boolean
        get() = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)

    override suspend fun requestGpsEnable(): Boolean {
        return suspendCancellableCoroutine {
            createGoogleApiLocationRequest(requireNotNull(activity))
            continuationCallback = it
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int) {
        continuationCallback?.let {
            continuationCallback = null
            it.resume(isGpsEnabled)
        }
    }

    private fun createGoogleApiLocationRequest(activity: Activity) {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val locationSettingsRequestBuilder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                .setAlwaysShow(true)
                .addLocationRequest(locationRequest)

        LocationServices.getSettingsClient(activity)
                .checkLocationSettings(locationSettingsRequestBuilder.build())
                .addOnCompleteListener(activity) { task ->
                    try {
                        task.getResult(ApiException::class.java)
                    } catch (exception: ApiException) {
                        when (exception.statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                                val resolvable = exception as ResolvableApiException
                                resolvable.startResolutionForResult(activity, GOOGLE_API_CLIENT_REQUEST_CODE)
                            } catch (ex: IntentSender.SendIntentException) {
                                Timber.e(ex)
                            } catch (ex: ClassCastException) {
                                Timber.e(ex)
                            }
                        }
                    }
                }
    }
}