package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import nl.biancavanschaik.android.museumkaart.data.rest.MuseumRestService
import nl.biancavanschaik.android.museumkaart.data.rest.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.util.rest.ApiResponse

class DetailsViewModel(
        private val museumRestService: MuseumRestService
) : ViewModel() {
    val museumId = MutableLiveData<String>()
    val museumDetails: LiveData<ApiResponse<MuseumDetails>> = Transformations.switchMap(museumId) { id ->
        id?.let { museumRestService.getDetails(it) }
    }
}