package nl.biancavanschaik.android.museumkaart.util.livedata

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

class EventLiveData<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {

        if (hasActiveObservers()) {
            Log.w("EventLiveData", "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer<T> {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }
}