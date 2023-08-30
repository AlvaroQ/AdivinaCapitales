package com.alvaroquintana.adivinacapitales.ui.info

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.base.BaseActivity
import com.alvaroquintana.adivinacapitales.common.viewBinding
import com.alvaroquintana.adivinacapitales.databinding.InfoActivityBinding
import com.alvaroquintana.adivinacapitales.utils.setSafeOnClickListener
import com.alvaroquintana.adivinacapitales.utils.showBanner
import com.alvaroquintana.adivinacapitales.utils.showBonificado
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics

class InfoActivity : BaseActivity() {
    private val binding by viewBinding(InfoActivityBinding::inflate)
    private var rewardedAd: RewardedAd? = null
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerInfo, InfoFragment.newInstance())
                .commitNow()
        }
        activity = this

        MobileAds.initialize(this)
        RewardedAd.load(this, getString(R.string.BONIFICADO_GAME), AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("GameActivity", adError.toString())
                FirebaseCrashlytics.getInstance().recordException(Throwable(adError.message))
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("GameActivity", "Ad was loaded.")
                rewardedAd = ad
            }
        })

        binding.appBar.btnBack.setSafeOnClickListener { finishAfterTransition() }
        binding.appBar.toolbarTitle.text = getString(R.string.info_title)
        binding.appBar.layoutLife.visibility = View.GONE
    }

    fun showAd(show: Boolean){
        showBanner(show, binding.adViewInfo)
    }

    fun showRewardedAd(show: Boolean){
        showBonificado(this, show, rewardedAd)
    }
}