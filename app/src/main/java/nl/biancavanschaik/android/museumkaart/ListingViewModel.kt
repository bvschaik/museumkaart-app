package nl.biancavanschaik.android.museumkaart

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import nl.biancavanschaik.android.museumkaart.data.MuseumDetailsRepository
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.util.livedata.InputLiveData

class ListingViewModel(
        private val museumDetailsRepository: MuseumDetailsRepository
) : ViewModel() {
    val listingId = InputLiveData<String>()
    val listing: LiveData<Listing> = Transformations.switchMap(listingId) { id ->
        id?.let { museumDetailsRepository.getListing(it) }
    }
}