package nl.biancavanschaik.android.museumkaart.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import nl.biancavanschaik.android.museumkaart.data.database.MuseumDao
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.data.database.model.Museum
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumSummary
import nl.biancavanschaik.android.museumkaart.data.rest.MuseumRestService
import nl.biancavanschaik.android.museumkaart.data.rest.model.RestMuseum as RestMuseumDetails
import nl.biancavanschaik.android.museumkaart.util.IsoDate
import nl.biancavanschaik.android.museumkaart.util.Resource
import nl.biancavanschaik.android.museumkaart.util.rest.ApiEmptyResponse
import nl.biancavanschaik.android.museumkaart.util.rest.ApiErrorResponse
import nl.biancavanschaik.android.museumkaart.util.rest.ApiResponse
import nl.biancavanschaik.android.museumkaart.util.rest.ApiSuccessResponse

class MuseumDetailsRepository(
        private val museumRestService: MuseumRestService,
        private val museumDao: MuseumDao
) {
    fun getAllMuseums(): LiveData<Resource<List<MuseumSummary>>> {
        return object : CachedResource<List<MuseumSummary>, List<RestMuseumDetails>>() {

            override fun loadFromCache(): LiveData<List<MuseumSummary>> {
                return museumDao.findAll()
            }

            override fun saveCallResult(item: List<RestMuseumDetails>, cacheItem: List<MuseumSummary>?) {
                museumDao.updateAll(item.toDatabaseObject(), cacheItem ?: emptyList())
            }

            override fun shouldFetch(data: List<MuseumSummary>?): Boolean {
                // TODO find a way to refresh only once a week or so
                return data == null || data.isEmpty()
                //return dateFetched == null || dateFetched < IsoDate.today() - 7
            }

            override fun createCall() =
                    MediatorLiveData<ApiResponse<List<RestMuseumDetails>>>().apply {
                        getNextPage(this, 0, emptyList())
                    }
        }.asLiveData()
    }

    private fun getNextPage(mediator: MediatorLiveData<ApiResponse<List<RestMuseumDetails>>>, pageId: Int, data: List<RestMuseumDetails>) {
        val restLiveData = museumRestService.getList(pageId)
        mediator.addSource(restLiveData) { pageData ->
            mediator.removeSource(restLiveData)
            when (pageData) {
                is ApiSuccessResponse ->
                        if (pageData.body.isEmpty()) {
                            mediator.postValue(ApiSuccessResponse(data))
                        } else {
                            getNextPage(mediator, pageId + 1, data + pageData.body)
                        }
                is ApiEmptyResponse -> mediator.postValue(ApiSuccessResponse(data))
                is ApiErrorResponse -> mediator.postValue(pageData)
            }
        }
    }

    fun getVisitedMuseums() = museumDao.findAllVisited()

    fun getWishListMuseums() = museumDao.findAllOnWishList()

    fun getDetails(museumId: String): LiveData<Resource<Museum>> {
        return object : CachedResource<Museum, RestMuseumDetails>() {

            override fun loadFromCache(): LiveData<Museum> {
                return museumDao.findById(museumId)
            }

            override fun saveCallResult(item: RestMuseumDetails, cacheItem: Museum?) {
                val museum = item.toDatabaseObject(cacheItem)
                if (cacheItem == null) {
                    museumDao.insert(museum)
                } else {
                    museumDao.update(museum, cacheItem)
                }
            }

            override fun shouldFetch(data: Museum?): Boolean {
                return data?.details?.dateFetched == null || data.details.dateFetched < IsoDate.today()
            }

            override fun createCall() = museumRestService.getDetails(museumId)
        }.asLiveData()
    }

    fun updateMuseum(museumDetails: MuseumDetails) {
        launch(CommonPool) {
            museumDao.update(museumDetails)
        }
    }

    fun getListing(listingId: String): LiveData<Listing> {
        // only use the database cache: we cannot get here without having fetched details
        return museumDao.findListingById(listingId)
    }
}