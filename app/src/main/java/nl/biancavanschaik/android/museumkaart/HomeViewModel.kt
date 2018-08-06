package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.ViewModel
import nl.biancavanschaik.android.museumkaart.data.MuseumDetailsRepository
import nl.biancavanschaik.android.museumkaart.util.livedata.EventLiveData

class HomeViewModel(
        museumDetailsRepository: MuseumDetailsRepository
) : ViewModel() {
    val allMuseums = museumDetailsRepository.getAllMuseums()
    val visitedMuseums = museumDetailsRepository.getVisitedMuseums()
    val wishListMuseums = museumDetailsRepository.getWishListMuseums()

    val selectedMuseumId = EventLiveData<String>()
}