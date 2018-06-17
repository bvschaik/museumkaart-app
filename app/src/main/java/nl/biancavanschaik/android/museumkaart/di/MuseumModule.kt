package nl.biancavanschaik.android.museumkaart.di

import nl.biancavanschaik.android.museumkaart.DetailsViewModel
import nl.biancavanschaik.android.museumkaart.data.MuseumDetailsRepository
import nl.biancavanschaik.android.museumkaart.data.rest.MuseumRestService
import nl.biancavanschaik.android.museumkaart.data.rest.MuseumRestServiceFactory
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val museumModule = applicationContext {
    bean { MuseumRestServiceFactory.getInstance() }
    bean { get<MuseumRestServiceFactory>().create(MuseumRestService::class.java) }
    bean { MuseumDetailsRepository(get()) }
    viewModel { DetailsViewModel(get()) }
}