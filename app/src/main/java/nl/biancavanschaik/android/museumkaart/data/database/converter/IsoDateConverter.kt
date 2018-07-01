package nl.biancavanschaik.android.museumkaart.data.database.converter

import android.arch.persistence.room.TypeConverter
import nl.biancavanschaik.android.museumkaart.util.IsoDate

class IsoDateConverter {
    @TypeConverter
    fun toIsoDate(value: String?) = IsoDate.fromIsoString(value)

    @TypeConverter
    fun fromIsoDate(value: IsoDate?) = value?.toIsoString()
}