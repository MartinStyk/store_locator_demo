package com.example.storelocator.manager.resources

import androidx.annotation.DimenRes

interface ResourcesManager {

    val currentLanguage: String

    fun getDimensionPixelSize(@DimenRes dimenRes: Int): Int

    fun readTextAssetsResource(fileName: String): String?
}