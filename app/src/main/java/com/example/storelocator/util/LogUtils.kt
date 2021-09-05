package com.example.storelocator.util

import android.util.Log
import com.example.storelocator.BuildConfig
import timber.log.Timber

object LogUtils {

    fun logTrees() = if (BuildConfig.DEBUG) arrayOf(Timber.DebugTree()) else arrayOf(FirebaseTree())

    // no firebase logging yet :-)
    class FirebaseTree : Timber.DebugTree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
//            FirebaseCrashlytics.getInstance().log(crashlyticsMessage(priority, tag, message))
//            if (priority >= Log.WARN && t != null) {
//                FirebaseCrashlytics.getInstance().recordException(t)
//            }
        }

        // format like in logcat "W/tag: warning message"
        private fun crashlyticsMessage(priority: Int, tag: String?, message: String): String {
            val priorityString = when (priority) {
                Log.ASSERT, Log.ERROR -> "E"
                Log.WARN -> "W"
                Log.INFO -> "I"
                Log.DEBUG -> "D"
                else -> "V"
            }
            return "$priorityString/$tag: $message"
        }

    }
}