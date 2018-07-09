package nl.biancavanschaik.android.museumkaart.data

import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.data.database.model.Museum
import nl.biancavanschaik.android.museumkaart.data.rest.model.Exhibition
import nl.biancavanschaik.android.museumkaart.data.rest.model.Listings
import nl.biancavanschaik.android.museumkaart.data.rest.model.MuseumDetails
import nl.biancavanschaik.android.museumkaart.util.IsoDate
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumDetails as DbMuseumDetails

fun List<MuseumDetails>.toDatabaseObject() = map {
    DbMuseumDetails(
            id = it.permanentid,
            numericId = it.id,
            name = it.displayname,
            address = it.streetandnumber,
            city = it.city,
            telephone = it.telephone,
            website = it.website,
            email = it.email,
            imagePath = it.path,
            admissionPrice = it.admissionprice,
            openingHours = it.openinghours,
            museumCardParticipant = it.museumcardparticipant,
            lat = it.lat,
            lon = it.lon
    )
}.distinctBy { it.id }

fun MuseumDetails.toDatabaseObject(cacheItem: Museum?): Museum {
    val details = DbMuseumDetails(
            id = permanentid,
            numericId = id,
            name = displayname,
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
    return Museum(details, listings.toDatabaseObject(details).distinctBy { it.id })
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
