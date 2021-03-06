package com.alvaroquintana.adivinacapitales.managers

import android.content.Context
import android.os.Bundle
import com.alvaroquintana.adivinacapitales.BuildConfig
import com.alvaroquintana.adivinacapitales.base.BaseActivity
import com.google.firebase.analytics.FirebaseAnalytics

object Analytics {
    lateinit var mFirebase: FirebaseAnalytics
    lateinit var ctx: Context

    fun initialize(ctx: Context) {
        this.ctx = ctx
        mFirebase = FirebaseAnalytics.getInstance(ctx.applicationContext)
    }

    fun analyticsScreenViewed(screenTitle: String) {

        logEvent(Event("screen_viewed")
            .with("uid", (ctx as BaseActivity).getUID())
            .with("screen_title", screenTitle)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    fun analyticsGameFinished(points: String) {

        logEvent(Event("game_finished")
            .with("uid", (ctx as BaseActivity).getUID())
            .with("points", points)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    fun analyticsClicked(btnDescription: String) {

        logEvent(Event("clicked")
            .with("uid", (ctx as BaseActivity).getUID())
            .with("component", btnDescription)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    fun analyticsAppRecommendedOpen(appName: String) {

        logEvent(Event("app_recommended_open")
            .with("uid", (ctx as BaseActivity).getUID())
            .with("app_name", appName)
            .with("app_version", BuildConfig.VERSION_NAME)
            .with("app_name", BuildConfig.APPLICATION_ID))
    }

    private fun logEvent(event: Event) {
        mFirebase.logEvent(event.eventName, event.bundle)
    }

    private class Event(val eventName: String) {
        val bundle = Bundle()
        fun with(key: String, value: String): Event {
            bundle.putString(key, value)
            return this
        }
    }

    //screen
    const val SCREEN_GAME = "screen_game"
    const val SCREEN_RESULT = "screen_result"
    const val SCREEN_RANKING = "screen_ranking"
    const val SCREEN_SELECT_GAME = "screen_select_game"
    const val SCREEN_DIALOG_SAVE_SCORE = "screen_dialog_save_score"

    // CLICKED
    const val BTN_PLAY_AGAIN = "btn_play_again"
    const val BTN_RATE = "btn_rate"
    const val BTN_RANKING = "btn_ranking"
    const val BTN_SHARE = "btn_share"
}