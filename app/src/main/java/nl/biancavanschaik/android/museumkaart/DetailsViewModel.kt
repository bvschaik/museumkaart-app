package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import nl.biancavanschaik.android.museumkaart.data.MuseumDetailsRepository
import nl.biancavanschaik.android.museumkaart.data.rest.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.util.Resource
import nl.biancavanschaik.android.museumkaart.util.livedata.InputLiveData

class DetailsViewModel(
        private val museumDetailsRepository: MuseumDetailsRepository
) : ViewModel() {
    val museumId = InputLiveData<String>()
    val museumDetails: LiveData<Resource<MuseumDetails>> = Transformations.switchMap(museumId) { id ->
        id?.let { museumDetailsRepository.getDetails(it) }
    }
}