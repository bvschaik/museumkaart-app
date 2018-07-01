package nl.biancavanschaik.android.museumkaart.data.database.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import nl.biancavanschaik.android.museumkaart.util.IsoDate

@Entity(tableName = "museums")
data class MuseumDetails(
        @PrimaryKey val id: String,
        val numericId: Long,
        val displayName: String,
        val address: String?,
        val city: String?,
        val telephone: String?,
        val website: String?,
        val email: String?,
        val imagePath: String?,
        val admissionPrice: String?,
        val openingHours: String?,
        val museumCardParticipant: Boolean,
        val lat: Double?,
        val lon: Double?,
        val dateFetched: IsoDate?,
        val visitedOn: IsoDate?,
        val wishList: Boolean = false
)