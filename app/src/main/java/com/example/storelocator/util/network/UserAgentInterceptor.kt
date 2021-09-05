package com.example.storelocator.util.network

import com.example.storelocator.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


private const val AGENT = "android,${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}}"

class UserAgentInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().addHeader("User-Agent", AGENT)
                .build()
        )
    }
}