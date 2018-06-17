package nl.biancavanschaik.android.museumkaart

import android.app.Application
import nl.biancavanschaik.android.museumkaart.di.museumModule
import org.koin.android.ext.android.startKoin

class MuseumkaartApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(museumModule))
    }
}