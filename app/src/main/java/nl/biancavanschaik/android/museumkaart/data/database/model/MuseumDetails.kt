package nl.biancavanschaik.android.museumkaart.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import nl.biancavanschaik.android.museumkaart.util.IsoDate

@Entity(tableName = "museums")
data class MuseumDetails(
        @PrimaryKey val id: String,
        val numericId: Long,
        val name: String,
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
        val dateFetched: IsoDate? = null,
        val visitedOn: IsoDate? = null,
        val wishList: Boolean = false,
        val active: Boolean = true
)