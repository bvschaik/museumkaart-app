package nl.biancavanschaik.android.museumkaart.data.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class Museum @JvmOverloads constructor(
    @Embedded
    val details: MuseumDetails,
    @Relation(parentColumn = "id", entityColumn = "museumId")
    var listings: List<Listing> = emptyList()
) {
    val permanentExhibition
            get() = listings.firstOrNull { it.type == Listing.Type.PERMANENT }
    val exhibitions
            get() = listings.filter { it.type == Listing.Type.EXHIBITION }
    val promotions
            get() = listings.filter { it.type == Listing.Type.PROMOTION }
    val events
            get() = listings.filter { it.type == Listing.Type.EVENT }
}