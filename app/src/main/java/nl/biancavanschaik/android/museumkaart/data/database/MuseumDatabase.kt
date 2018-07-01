package nl.biancavanschaik.android.museumkaart.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import nl.biancavanschaik.android.museumkaart.data.database.converter.IsoDateConverter
import nl.biancavanschaik.android.museumkaart.data.database.converter.ListingTypeConverter
import nl.biancavanschaik.android.museumkaart.data.database.model.Listing
import nl.biancavanschaik.android.museumkaart.data.database.model.MuseumDetails

@Database(
        entities = [MuseumDetails::class, Listing::class],
        version = 1
)
@TypeConverters(IsoDateConverter::class, ListingTypeConverter::class)
abstract class MuseumDatabase: RoomDatabase() {
    abstract fun museumDao(): MuseumDao

    companion object {
        fun getInstance(context: Context) =
                Room.databaseBuilder(context, MuseumDatabase::class.java, "museums.db")
                        .build()
    }
}
