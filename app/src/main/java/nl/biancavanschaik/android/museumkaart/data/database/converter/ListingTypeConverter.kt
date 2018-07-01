package nl.biancavanschaik.android.museumkaart.data.database.converter

import android.arch.persistence.room.TypeConverter
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing

class ListingTypeConverter {
    @TypeConverter
    fun toEnum(value: Int?) = Listing.Type.fromId(value)

    @TypeConverter
    fun fromEnum(value: Listing.Type?) = value?.typeId
}