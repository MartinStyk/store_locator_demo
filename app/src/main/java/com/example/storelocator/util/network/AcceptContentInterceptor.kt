package com.example.storelocator.util.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AcceptContentInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().addHeader("Accept", "application/json").build()
        )
    }
}