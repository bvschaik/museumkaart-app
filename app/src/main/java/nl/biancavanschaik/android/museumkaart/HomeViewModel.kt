package nl.biancavanschaik.android.museumkaart

import android.arch.lifecycle.ViewModel
import nl.biancavanschaik.android.museumkaart.data.MuseumDetailsRepository

class HomeViewModel(
        museumDetailsRepository: MuseumDetailsRepository
) : ViewModel() {
    val allMuseums = museumDetailsRepository.getAllMuseums()
}