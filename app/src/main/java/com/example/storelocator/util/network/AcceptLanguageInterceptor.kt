package com.example.storelocator.util.network

import com.example.storelocator.manager.resources.ResourcesManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

class AcceptLanguageInterceptor @Inject constructor(private val resourcesManager: ResourcesManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().addHeader("Accept-Language", resourcesManager.currentLanguage).build()
        )
    }
}