package com.example.storelocator.dependencyinjection

import com.example.storelocator.BuildConfig
import com.example.storelocator.api.PaySafeCardApi
import com.example.storelocator.dependencyinjection.util.PaySafeApi
import com.example.storelocator.manager.resources.ResourcesManager
import com.example.storelocator.util.network.AcceptContentInterceptor
import com.example.storelocator.util.network.AcceptLanguageInterceptor
import com.example.storelocator.util.network.ClientApplicationKeyInterceptor
import com.example.storelocator.util.network.UserAgentInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkingModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideOkHttpClient(
        acceptLanguageInterceptor: AcceptLanguageInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }
                    .apply { level = HttpLoggingInterceptor.Level.BODY })
        }
        builder.addInterceptor(acceptLanguageInterceptor)
        builder.addInterceptor(userAgentInterceptor)

        return builder.build()
    }

    @Provides
    @PaySafeApi
    fun provideOkHttpClientForPaySafeApi(
        okHttpClient: OkHttpClient,
        clientAppInterceptor: ClientApplicationKeyInterceptor,
        acceptContentInterceptor: AcceptContentInterceptor,
        resourcesManager: ResourcesManager,
        gson: Gson,
    ): OkHttpClient {
        val pins = gson.fromJson<List<String>>(
            resourcesManager.readTextAssetsResource(PaySafeCardApi.CERTIFICATE_PINS) ?: "", object :
                TypeToken<List<String>>() {}.type
        )
        return okHttpClient.newBuilder()
            .certificatePinner(
                CertificatePinner.Builder()
                    .add("**.paysafecard.com", *pins.toTypedArray())
                    .build()
            )
            .addInterceptor(clientAppInterceptor)
            .addInterceptor(acceptContentInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providePaySafeCardApi(
        @PaySafeApi okHttpClient: OkHttpClient,
        gson: Gson,
    ): PaySafeCardApi = Retrofit.Builder()
        .baseUrl(PaySafeCardApi.URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(PaySafeCardApi::class.java)

}
