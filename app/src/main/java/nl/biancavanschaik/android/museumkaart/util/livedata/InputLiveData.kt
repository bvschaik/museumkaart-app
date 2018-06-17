package nl.biancavanschaik.android.museumkaart.util.livedata

import android.arch.lifecycle.MutableLiveData

class InputLiveData<T> : MutableLiveData<T>() {

    override fun setValue(value: T?) {
        if (value != this.value) {
            super.setValue(value)
        }
    }
}