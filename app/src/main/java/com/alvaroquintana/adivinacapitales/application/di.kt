package com.alvaroquintana.adivinacapitales.application

import com.alvaroquintana.adivinacapitales.datasource.FirestoreDataSourceImpl
import com.alvaroquintana.data.datasource.FirestoreDataSource
import android.app.Application
import com.alvaroquintana.adivinacapitales.ui.game.GameViewModel
import com.alvaroquintana.adivinacapitales.ui.result.ResultViewModel
import com.alvaroquintana.adivinacapitales.ui.select.SelectViewModel
import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.adivinacapitales.datasource.DataBaseSourceImpl
import com.alvaroquintana.adivinacapitales.ui.info.InfoViewModel
import com.alvaroquintana.adivinacapitales.ui.ranking.RankingViewModel
import com.alvaroquintana.data.repository.AppsRecommendedRepository
import com.alvaroquintana.data.repository.CountryRepository
import com.alvaroquintana.data.repository.IntegrityRepository
import com.alvaroquintana.data.repository.RankingRepository
import com.alvaroquintana.usecases.GetAppsRecommended
import com.alvaroquintana.usecases.GetCountryById
import com.alvaroquintana.usecases.GetCountryList
import com.alvaroquintana.usecases.GetRankingScore
import com.alvaroquintana.usecases.GetRecordScore
import com.alvaroquintana.usecases.SaveIntegrity
import com.alvaroquintana.usecases.SaveTopScore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun Application.initDI() {
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(appModule, dataModule, scopesModule)
    }
}

private val appModule = module {
    factory { Firebase.firestore }
    single<CoroutineDispatcher> { Dispatchers.Main }
    factory<DataBaseSource> { DataBaseSourceImpl() }
    factory<FirestoreDataSource> { FirestoreDataSourceImpl(get()) }
}

val dataModule = module {
    factory { CountryRepository(get()) }
    factory { AppsRecommendedRepository(get()) }
    factory { RankingRepository(get()) }
    factory { IntegrityRepository(get()) }
}

private val scopesModule = module {
    viewModel { SelectViewModel(get()) }
    viewModel { GameViewModel(get()) }
    viewModel { RankingViewModel(get()) }
    viewModel { ResultViewModel(get(), get(), get()) }
    viewModel { InfoViewModel(get()) }

    factory { SaveIntegrity(get()) }
    factory { GetCountryById(get()) }
    factory { GetRecordScore(get()) }
    factory { GetAppsRecommended(get()) }
    factory { SaveTopScore(get()) }
    factory { GetRankingScore(get()) }
    factory { GetCountryList(get()) }
}
