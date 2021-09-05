package com.example.storelocator.util.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppCoroutineScope @Inject constructor(dispatcherProvider: DispatcherProvider) {
    val main = CoroutineScope(SupervisorJob() + dispatcherProvider.main())
    val default = CoroutineScope(SupervisorJob() + dispatcherProvider.default())
    val io = CoroutineScope(SupervisorJob() + dispatcherProvider.io())
}