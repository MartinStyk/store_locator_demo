package com.example.storelocator.manager.resources

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DimenRes
import com.example.storelocator.dependencyinjection.util.ForApplication
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject

class ResourcesManagerImpl @Inject constructor(
    @ForApplication private val resources: Resources,
    @ForApplication private val context: Context
) : ResourcesManager {

    override val currentLanguage: String
        get() = Locale.getDefault().language

    override fun getDimensionPixelSize(@DimenRes dimenRes: Int) =
        resources.getDimensionPixelSize(dimenRes)

    override fun readTextAssetsResource(fileName: String): String? =
        try {
            context.assets.open(fileName).bufferedReader().readText()
        } catch (exception: IOException) {
            Timber.e(exception)
            null
        }

}