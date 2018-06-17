package nl.biancavanschaik.android.museumkaart.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import nl.biancavanschaik.android.museumkaart.data.rest.MuseumRestService
import nl.biancavanschaik.android.museumkaart.data.rest.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.util.Resource
import nl.biancavanschaik.android.museumkaart.util.rest.ApiEmptyResponse
import nl.biancavanschaik.android.museumkaart.util.rest.ApiErrorResponse
import nl.biancavanschaik.android.museumkaart.util.rest.ApiSuccessResponse

class MuseumDetailsRepository(
        private val museumRestService: MuseumRestService
) {
    fun getDetails(museumId: String): LiveData<Resource<MuseumDetails>> {
        return MediatorLiveData<Resource<MuseumDetails>>().apply {
            value = Resource.loading()
            addSource(museumRestService.getDetails(museumId)) { response ->
                when (response) {
                    is ApiSuccessResponse -> value = Resource.success(response.body)
                    is ApiErrorResponse -> value = Resource.error(response.errorMessage)
                }
            }
        }
    }
}