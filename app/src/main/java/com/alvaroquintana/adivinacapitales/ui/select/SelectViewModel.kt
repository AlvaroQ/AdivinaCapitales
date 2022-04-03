package com.alvaroquintana.adivinacapitales.ui.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinacapitales.common.ScopedViewModel
import com.alvaroquintana.adivinacapitales.managers.Analytics

class SelectViewModel : ScopedViewModel() {

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_SELECT_GAME)
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

    sealed class Navigation {
        object GameByFlag : Navigation()
        object GameByCountry : Navigation()
        object Settings : Navigation()
    }
}