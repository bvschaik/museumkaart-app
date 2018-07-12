package nl.biancavanschaik.android.museumkaart.data.database.model

import nl.biancavanschaik.android.museumkaart.util.IsoDate

data class MuseumSummary(
    val id: String,
    val name: String,
    val city: String,
    val lat: Double?,
    val lon: Double?,
    val visitedOn: IsoDate?,
    val wishList: Boolean = false
)