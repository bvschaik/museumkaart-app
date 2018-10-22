package nl.biancavanschaik.android.museumkaart.util.livedata

import androidx.lifecycle.MutableLiveData

class InputLiveData<T> : MutableLiveData<T>() {

    override fun setValue(value: T?) {
        if (value != this.value) {
            super.setValue(value)
        }
    }
}