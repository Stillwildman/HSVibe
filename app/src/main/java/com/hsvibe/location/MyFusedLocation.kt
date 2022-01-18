package com.hsvibe.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.location.Location
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.hsvibe.AppController
import com.hsvibe.utilities.L
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import kotlin.coroutines.resumeWithException

/**
 * Created by Vincent on 2021/7/5.
 */
object MyFusedLocation {

    private const val TAG = "MyFusedLocation"

    const val REQUEST_CHECK_LOCATION_SETTINGS = 1002

    private var fusedClient: WeakReference<FusedLocationProviderClient>? = null

    private fun getFusedClient(): FusedLocationProviderClient {
        if (fusedClient?.get() == null) {
            fusedClient = WeakReference(LocationServices.getFusedLocationProviderClient(AppController.getAppContext()))
        }
        return fusedClient!!.get()!!
    }

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    fun checkLocationSetting(activity: Activity, listener: OnSuccessListener<LocationSettingsResponse>) {
        val builder = LocationSettingsRequest.Builder()

        builder.addLocationRequest(getLocationRequest())

        val settingsClient = LocationServices.getSettingsClient(AppController.getAppContext())

        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener(listener)

        task.addOnFailureListener { e: Exception? ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    e.startResolutionForResult(activity, REQUEST_CHECK_LOCATION_SETTINGS)
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @SuppressLint("MissingPermission")
    suspend fun awaitLastLocation(): Location? {
        // Create a new coroutine that can be cancelled
        return suspendCancellableCoroutine { continuation ->
            val lastLocationTask = getFusedClient().lastLocation

            // Add listeners that will resume the execution of this coroutine
            lastLocationTask
                .addOnSuccessListener { location ->
                    // Resume coroutine and return location
                    L.i(TAG, "LastLocation:\n$location")
                    location?.let {
                        continuation.resume(it) { throwableCause ->
                            throwableCause.printStackTrace()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Resume the coroutine by throwing an exception
                    continuation.resumeWithException(e)
                }
            // End of the suspendCancellableCoroutine block. This suspends the coroutine until one of the callbacks calls the continuation parameter.
        }
    }
}