package com.alvaroquintana.adivinacapitales.ui.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinacapitales.common.ScopedViewModel
import com.alvaroquintana.adivinacapitales.managers.Analytics
import com.alvaroquintana.domain.Integrity
import com.alvaroquintana.usecases.SaveIntegrity
import kotlinx.coroutines.launch

class SelectViewModel(private val saveIntegrity: SaveIntegrity) : ScopedViewModel() {

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_SELECT_GAME)
    }
    fun savePayloadIntegrity(payload: String) {
        launch {
            saveIntegrity.invoke(Integrity(payload))
        }
    }
    fun navigateToGameByFlag() {
        _navigation.value = Navigation.GameByFlag
    }

    fun navigateToGameByCountry() {
        _navigation.value = Navigation.GameByCountry
    }

    fun navigateToSettings() {
        _navigation.value = Navigation.Settings
    }

    fun navigateToLearn() {
        _navigation.value = Navigation.Learn
    }

    sealed class Navigation {
        object GameByFlag : Navigation()
        object GameByCountry : Navigation()
        object Settings : Navigation()
        object Learn : Navigation()
    }
}