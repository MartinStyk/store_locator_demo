package com.example.storelocator.dependencyinjection

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storelocator.manager.location.LocationRequestManager
import com.example.storelocator.manager.location.LocationRequestManagerImpl
import com.example.storelocator.manager.permission.PermissionManager
import com.example.storelocator.manager.permission.PermissionsManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@InstallIn(ActivityComponent::class)
@Module
class ActivityCommonModule {

    @Provides
    @ActivityScoped
    fun provideActivityContext(activity: Activity): Context = activity.baseContext

    @Provides
    @ActivityScoped
    fun providesResources(activity: Activity): AppCompatActivity = activity as AppCompatActivity

    @Provides
    @ActivityScoped
    fun providePermissionsManager(activity: AppCompatActivity): PermissionManager {
        val permissionsManager: PermissionsManagerImpl = ViewModelProvider(activity, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PermissionsManagerImpl(activity.applicationContext) as T
            }
        }).get(PermissionsManagerImpl::class.java)
        permissionsManager.bind(activity)
        return permissionsManager
    }

    @Provides
    @ActivityScoped
    fun provideLocationRequester(activity: AppCompatActivity, locationManager: LocationManager): LocationRequestManager {
        val locationRequestManager: LocationRequestManagerImpl = ViewModelProvider(activity, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return LocationRequestManagerImpl(locationManager) as T
            }
        }).get(LocationRequestManagerImpl::class.java)
        locationRequestManager.bind(activity)
        return locationRequestManager
    }


}