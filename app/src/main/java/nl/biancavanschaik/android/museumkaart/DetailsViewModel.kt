package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import nl.biancavanschaik.android.museumkaart.data.MuseumDetailsRepository
import nl.biancavanschaik.android.museumkaart.data.database.model.Museum
import nl.biancavanschaik.android.museumkaart.util.Resource
import nl.biancavanschaik.android.museumkaart.util.livedata.EventLiveData
import nl.biancavanschaik.android.museumkaart.util.livedata.InputLiveData

class DetailsViewModel(
        private val museumDetailsRepository: MuseumDetailsRepository
) : ViewModel() {
    val museumId = InputLiveData<String>()
    val museumDetails: LiveData<Resource<Museum>> = Transformations.switchMap(museumId) { id ->
        id?.let { museumDetailsRepository.getDetails(it) }
    }

    val selectedListingId = EventLiveData<String>()

    fun setWishList(onWishList: Boolean) {
        museumDetails.value?.data?.details?.let {
            val newDetails = it.copy(wishList = onWishList)
            museumDetailsRepository.updateMuseum(newDetails)
        }
    }
}