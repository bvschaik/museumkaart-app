package nl.biancavanschaik.android.museumkaart.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import nl.biancavanschaik.android.museumkaart.util.IsoDate

@Entity(tableName = "listings")
data class Listing(
        @PrimaryKey val id: String,
        val numericId: Long,
        val museumId: String,
        val type: Type,
        val startDate: IsoDate?,
        val endDate: IsoDate?,
        val name: String,
        val locationId: Int?,
        val description: String,
        val openingHours: String?,
        val admissionPrice: String?,
        val imagePath: String?,
        val city: String?
) {
    enum class Type(val typeId: Int) {
        PERMANENT(1),
        EXHIBITION(2),
        PROMOTION(3),
        EVENT(4);

        companion object {
            fun fromId(id: Int?): Type? {
                return values().find { it.typeId == id }
            }
        }
    }
}