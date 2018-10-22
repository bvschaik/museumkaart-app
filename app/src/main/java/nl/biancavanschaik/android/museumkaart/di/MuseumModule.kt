package nl.biancavanschaik.android.museumkaart.di

import nl.biancavanschaik.android.museumkaart.DetailsViewModel
import nl.biancavanschaik.android.museumkaart.HomeViewModel
import nl.biancavanschaik.android.museumkaart.ListingViewModel
import nl.biancavanschaik.android.museumkaart.data.MuseumDetailsRepository
import nl.biancavanschaik.android.museumkaart.data.database.MuseumDatabase
import nl.biancavanschaik.android.museumkaart.data.rest.MuseumRestService
import nl.biancavanschaik.android.museumkaart.data.rest.MuseumRestServiceFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val museumModule = module {
    single { MuseumDatabase.getInstance(androidApplication()) }
    single { get<MuseumDatabase>().museumDao() }

    single { MuseumRestServiceFactory.getInstance() }
    single { get<MuseumRestServiceFactory>().create(MuseumRestService::class.java) }
    single { MuseumDetailsRepository(get(), get()) }

    viewModel { HomeViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
    viewModel { ListingViewModel(get()) }
}