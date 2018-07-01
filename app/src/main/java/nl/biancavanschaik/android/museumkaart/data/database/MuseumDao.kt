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

@Dao
interface MuseumDao {
    @Transaction
    @Query("select * from museums where id = :id")
    fun findById(id: String): LiveData<Museum>

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
        Log.d(TAG, "Deleting listings: $toDelete")
        Log.d(TAG, "Inserting listings: $toInsert")
        Log.d(TAG, "Updating listings: $toUpdate")
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
}
private const val TAG = "UPDATE"