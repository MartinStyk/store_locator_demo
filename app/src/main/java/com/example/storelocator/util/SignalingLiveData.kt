package com.example.storelocator.util

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 * <p>
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active.
 * <p>
 * Note that only one observer is going to be notified of changes.
 */
open class SignalingLiveData<T> : MutableLiveData<T> {

    constructor() : super()
    constructor(value: T) : super(value)

    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            throw IllegalAccessException("For register multiple observers use MultipleSignalingLiveData.")
        }

        super.observe(owner, { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    @AnyThread
    override fun postValue(t: T?) {
        pending.set(true)
        super.postValue(t)
    }

    fun clear() {
        value = null
        pending.set(false)
    }
}

typealias ValuelessLiveData = LiveData<Unit>

class ValuelessSignalingLiveData : SignalingLiveData<Unit>() {
    fun call() {
        value = Unit
    }

    fun postCall() {
        postValue(Unit)
    }
}