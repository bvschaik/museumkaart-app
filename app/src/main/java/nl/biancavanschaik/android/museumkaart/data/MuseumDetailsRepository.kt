package nl.biancavanschaik.android.museumkaart.data

import android.arch.lifecycle.LiveData
import nl.biancavanschaik.android.museumkaart.data.database.MuseumDao
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.data.database.model.Museum
import nl.biancavanschaik.android.museumkaart.data.rest.MuseumRestService
import nl.biancavanschaik.android.museumkaart.data.rest.model.Exhibition
import nl.biancavanschaik.android.museumkaart.data.rest.model.Listings
import nl.biancavanschaik.android.museumkaart.data.rest.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.util.IsoDate
import nl.biancavanschaik.android.museumkaart.util.Resource
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumDetails as DbMuseumDetails

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

    private fun MuseumDetails.toDatabaseObject(cacheItem: Museum?): Museum {
        val details = DbMuseumDetails(
                id = permanentid,
                numericId = id,
                displayName = displayname,
                address = streetandnumber,
                city = city,
                telephone = telephone,
                website = website,
                email = email,
                imagePath = path,
                admissionPrice = admissionprice,
                openingHours = openinghours,
                museumCardParticipant = museumcardparticipant,
                lat = lat,
                lon = lon,
                dateFetched = IsoDate.today(),
                visitedOn = cacheItem?.details?.visitedOn,
                wishList = cacheItem?.details?.wishList == true
        )
        return Museum(details, listings.toDatabaseObject(details))
    }

    private fun Listings.toDatabaseObject(museum: DbMuseumDetails) =
            permanent.map { it.toDatabaseObject(museum, Listing.Type.PERMANENT) } +
                    exhibition.map { it.toDatabaseObject(museum, Listing.Type.EXHIBITION) } +
                    promotion.map { it.toDatabaseObject(museum, Listing.Type.PROMOTION) } +
                    event.map { it.toDatabaseObject(museum, Listing.Type.EVENT) }

    private fun Exhibition.toDatabaseObject(museum: DbMuseumDetails, type: Listing.Type) =
            Listing(
                    id = permanentid,
                    numericId = id,
                    museumId = museum.id,
                    type = type,
                    startDate = IsoDate.fromIsoString(startdate),
                    endDate = IsoDate.fromIsoString(enddate),
                    name = name,
                    locationId = locationid,
                    description = description,
                    openingHours = openinghours,
                    admissionPrice = admissionprice,
                    imagePath = path,
                    city = city
            )
}