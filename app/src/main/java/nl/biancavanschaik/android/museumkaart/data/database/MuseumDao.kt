package nl.biancavanschaik.android.museumkaart.data.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import android.arch.persistence.room.Update
import android.util.Log
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.data.database.model.Museum
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumSummary

@Dao
interface MuseumDao {

    @Query("select id, name, lat, lon, visitedOn, wishList from museums where active = 1")
    fun findAll(): LiveData<List<MuseumSummary>>

    @Query("select id, name, lat, lon, visitedOn, wishList from museums where active = 1 and visitedOn is not null")
    fun findAllVisited(): LiveData<List<MuseumSummary>>

    @Query("select id, name, lat, lon, visitedOn, wishList from museums where active = 1 and wishList = 1")
    fun findAllOnWishList(): LiveData<List<MuseumSummary>>

    @Transaction
    @Query("select * from museums where id = :id")
    fun findById(id: String): LiveData<Museum>

    @Query("select * from listings where id = :id")
    fun findListingById(id: String): LiveData<Listing>

    @Transaction
    fun updateAll(museums: List<MuseumDetails>, oldSummaries: List<MuseumSummary>) {
        val groups = museums.groupBy { oldSummaries.find { old -> it.id == old.id } == null }
        val toInsert = groups[true] ?: emptyList()
        val toUpdate = groups[false] ?: emptyList()
        val toDelete = oldSummaries.filter { museums.find { new -> it.id == new.id } == null }
        Log.d(TAG, "Updating museum list")
        Log.d(TAG, "Inserting: ${toInsert.joinToString { it.id }}")
        Log.d(TAG, "Updating: ${toUpdate.joinToString { it.id }}")
        Log.d(TAG, "Deleting: ${toDelete.joinToString { it.id }}")
        toInsert.forEach { insert(it) }
        toUpdate.forEach { update(getMuseumDetails(it.id).merge(it)) }
        toDelete.forEach { update(getMuseumDetails(it.id).copy(active = false)) }
    }

    private fun MuseumDetails.merge(newDetails: MuseumDetails) = copy(
            name = newDetails.name,
            address = newDetails.address,
            city = newDetails.city,
            telephone = newDetails.telephone,
            website = newDetails.website,
            email = newDetails.email,
            imagePath = newDetails.imagePath,
            admissionPrice = newDetails.admissionPrice,
            openingHours = newDetails.openingHours,
            museumCardParticipant = newDetails.museumCardParticipant,
            lat = newDetails.lat,
            lon = newDetails.lon
    )

    @Transaction
    fun insert(museum: Museum) {
        insert(museum.details)
        museum.listings.forEach { insert(it) }
    }

    @Transaction
    fun update(museum: Museum, oldMuseum: Museum) {
        Log.d(TAG, "Updating museum: ${museum.details}")
        update(museum.details)
        val toInsert = museum.listings.filter { oldMuseum.listings.find { old -> it.id == old.id } == null }
        val toUpdate = museum.listings.filter { oldMuseum.listings.find { old -> it.id == old.id } != null }
        val toDelete = oldMuseum.listings.filter { museum.listings.find { new -> it.id == new.id } == null }
        toDelete.forEach { delete(it) }
        toInsert.forEach { insert(it) }
        toUpdate.forEach { update(it) }
    }

    @Insert
    fun insert(museum: MuseumDetails)

    @Update
    fun update(museum: MuseumDetails)

    @Insert
    fun insert(listing: Listing)

    @Update
    fun update(listing: Listing)

    @Delete
    fun delete(listing: Listing)

    @Query("select * from museums where id = :id")
    fun getMuseumDetails(id: String): MuseumDetails
}

private const val TAG = "UPDATE"