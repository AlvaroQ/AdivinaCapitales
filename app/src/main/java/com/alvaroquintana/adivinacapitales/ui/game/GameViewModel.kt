package com.alvaroquintana.adivinacapitales.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alvaroquintana.adivinacapitales.common.ScopedViewModel
import com.alvaroquintana.adivinacapitales.managers.Analytics
import com.alvaroquintana.adivinacapitales.utils.Constants.TOTAL_COUNTRIES
import com.alvaroquintana.domain.Country
import com.alvaroquintana.usecases.GetCountryById
import kotlinx.coroutines.launch

class GameViewModel(private val getCountryById: GetCountryById) : ScopedViewModel() {
    private var randomCountries = mutableListOf<Int>()
    private lateinit var country: Country

    private val _question = MutableLiveData<Country>()
    val question: LiveData<Country> = _question

    private val _responseOptions = MutableLiveData<MutableList<String>>()
    val responseOptions: LiveData<MutableList<String>> = _responseOptions

    private val _progress = MutableLiveData<UiModel>()
    val progress: LiveData<UiModel> = _progress

    private val _navigation = MutableLiveData<Navigation>()
    val navigation: LiveData<Navigation> = _navigation

    private val _showingAds = MutableLiveData<UiModel>()
    val showingAds: LiveData<UiModel> = _showingAds

    init {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_GAME)
        generateNewStage()
        _showingAds.value = UiModel.ShowBannerAd(true)
    }

    fun showRewardedAd() {
        _showingAds.value = UiModel.ShowReewardAd(true)
    }

    fun generateNewStage() {
        launch {
            _progress.value = UiModel.Loading(true)

            /** Generate question */
            val numRandomMain = generateRandomWithExcusion(0, TOTAL_COUNTRIES, *randomCountries.toIntArray())
            randomCountries.add(numRandomMain)

            country = getCountry(numRandomMain)

            /** Generate responses */
            val numRandomMainPosition = generateRandomWithExcusion(0, 3)

            val numRandomOption1 = generateRandomWithExcusion(1, TOTAL_COUNTRIES, numRandomMain)
            val option1: Country = getCountry(numRandomOption1)
            val numRandomPosition1 = generateRandomWithExcusion(0, 3, numRandomMainPosition)

            val numRandomOption2 = generateRandomWithExcusion(1, TOTAL_COUNTRIES, numRandomMain, numRandomOption1)
            val option2: Country = getCountry(numRandomOption2)
            val numRandomPosition2 = generateRandomWithExcusion(0, 3, numRandomMainPosition, numRandomPosition1)

            val numRandomOption3 = generateRandomWithExcusion(1, TOTAL_COUNTRIES, numRandomMain, numRandomOption1, numRandomOption2)
            val option3: Country = getCountry(numRandomOption3)
            val numRandomPosition3 = generateRandomWithExcusion(0, 3, numRandomMainPosition, numRandomPosition1, numRandomPosition2)

            /** Save value */
            val optionList = mutableListOf("", "", "", "")
            optionList[numRandomMainPosition] = country.capital!!
            optionList[numRandomPosition1] = option1.capital!!
            optionList[numRandomPosition2] = option2.capital!!
            optionList[numRandomPosition3] = option3.capital!!

            _responseOptions.value = optionList
            _question.value = country
            _progress.value = UiModel.Loading(false)
        }
    }

    private suspend fun getCountry(id: Int): Country {
        return getCountryById.invoke(id)
    }

    fun navigateToResult(points: String) {
        Analytics.analyticsGameFinished(points)
        _navigation.value = Navigation.Result
    }

    fun getCountry() : Country {
        return country
    }

    private fun generateRandomWithExcusion(start: Int, end: Int, vararg exclude: Int): Int {
        var numRandom = (start..end).random()
        while(exclude.contains(numRandom)){
            numRandom = (start..end).random()
        }
        return numRandom
    }

    fun navigateToExtraLifeDialog() {
        _navigation.value = Navigation.ExtraLifeDialog
    }

    sealed class UiModel {
        data class Loading(val show: Boolean) : UiModel()
        data class ShowBannerAd(val show: Boolean) : UiModel()
        data class ShowReewardAd(val show: Boolean) : UiModel()
    }

    sealed class Navigation {
        object Result : Navigation()
        object ExtraLifeDialog : Navigation()
    }
}