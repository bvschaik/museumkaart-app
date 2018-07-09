package nl.biancavanschaik.android.museumkaart.data

import android.arch.lifecycle.LiveData
import nl.biancavanschaik.android.museumkaart.data.database.MuseumDao
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.data.database.model.Museum
import nl.biancavanschaik.android.museumkaart.data.rest.MuseumRestService
import nl.biancavanschaik.android.museumkaart.data.rest.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.util.IsoDate
import nl.biancavanschaik.android.museumkaart.util.Resource

class MuseumDetailsRepository(
        private val museumRestService: MuseumRestService,
        private val museumDao: MuseumDao
) {
    fun getDetails(museumId: String): LiveData<Resource<Museum>> {
        return object : CachedResource<Museum, MuseumDetails>() {

            override fun loadFromCache(): LiveData<Museum> {
                return museumDao.findById(museumId)
            }

            override fun saveCallResult(item: MuseumDetails, cacheItem: Museum?) {
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

    fun getListing(listingId: String): LiveData<Listing> {
        // only use the database cache: we cannot get here without having fetched details
        return museumDao.findListingById(listingId)
    }
}