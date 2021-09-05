package com.example.storelocator.dependencyinjection

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import com.example.storelocator.dependencyinjection.util.ForApplication
import com.example.storelocator.manager.location.LocationManager
import com.example.storelocator.manager.location.LocationManagerImpl
import com.example.storelocator.manager.paysafe.stores.StoresManager
import com.example.storelocator.manager.paysafe.stores.StoresManagerImpl
import com.example.storelocator.manager.resources.ResourcesManager
import com.example.storelocator.manager.resources.ResourcesManagerImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.location.LocationManager as AndroidLocationManager

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {

    @Provides
    @Singleton
    @ForApplication
    fun provideApplicationContext(application: Application): Context =
        application.applicationContext

    @Provides
    @Singleton
    @ForApplication
    fun providesResources(application: Application): Resources = application.resources

    @Provides
    @Singleton
    fun providePackageManager(application: Application): PackageManager = application.packageManager

    @Provides
    @Singleton
    fun provideContentResolver(application: Application): ContentResolver =
        application.contentResolver

    @Provides
    @Singleton
    fun provideAndroidLocationManager(application: Application): AndroidLocationManager =
        application.getSystemService(Context.LOCATION_SERVICE) as AndroidLocationManager

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(application: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @Provides
    @Singleton
    fun provideLocationManager(locationManagerImpl: LocationManagerImpl): LocationManager =
        locationManagerImpl

    @Provides
    @Singleton
    fun provideStoresManager(storesManagerImpl: StoresManagerImpl): StoresManager =
        storesManagerImpl

    @Provides
    @Singleton
    fun provideResourcesManager(resourcesManagerImpl: ResourcesManagerImpl): ResourcesManager = resourcesManagerImpl

}
