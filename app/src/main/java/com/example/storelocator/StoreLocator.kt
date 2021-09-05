package com.example.storelocator

import android.app.Application
import com.example.storelocator.util.LogUtils
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class StoreLocator : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(*LogUtils.logTrees())
    }
}