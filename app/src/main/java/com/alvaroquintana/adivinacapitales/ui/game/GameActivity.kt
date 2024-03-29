package com.alvaroquintana.adivinacapitales.ui.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.base.BaseActivity
import com.alvaroquintana.adivinacapitales.common.startActivity
import com.alvaroquintana.adivinacapitales.common.viewBinding
import com.alvaroquintana.adivinacapitales.databinding.GameActivityBinding
import com.alvaroquintana.adivinacapitales.ui.select.SelectActivity
import com.alvaroquintana.adivinacapitales.utils.setSafeOnClickListener
import com.alvaroquintana.adivinacapitales.utils.showBanner
import com.alvaroquintana.adivinacapitales.utils.showBonificado
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.crashlytics.FirebaseCrashlytics


class GameActivity : BaseActivity() {
    private val binding by viewBinding(GameActivityBinding::inflate)
    private var rewardedAd: RewardedAd? = null
    private lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerGame, GameFragment.newInstance())
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

        binding.appBar.btnBack.setSafeOnClickListener {
            startActivity<SelectActivity> {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        writeStage(1)
    }

    fun writeStage(stage: Int) {
        binding.appBar.toolbarTitle.text = stage.toString()
    }

    fun writeDeleteLife(life: Int) {
        when(life) {
            3 -> {
                binding.appBar.lifeThree.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_on))
                binding.appBar.lifeSecond.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_on))
                binding.appBar.lifeFirst.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_on))
            }
            2 -> {
                binding.appBar.lifeThree.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_xy_collapse))

                binding.appBar.lifeThree.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_off))
                binding.appBar.lifeSecond.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_on))
                binding.appBar.lifeFirst.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_on))
            }
            1 -> {
                binding.appBar.lifeSecond.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_xy_collapse))

                binding.appBar.lifeThree.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_off))
                binding.appBar.lifeSecond.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_off))
                binding.appBar.lifeFirst.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_on))
            }
            0 -> {
                binding.appBar.lifeFirst.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_xy_collapse))

                // GAME OVER
                binding.appBar.lifeThree.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_off))
                binding.appBar.lifeSecond.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_off))
                binding.appBar.lifeFirst.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_life_off))
            }
        }
    }

    fun showBannerAd(show: Boolean){
        showBanner(show, binding.adViewGame)
    }

    fun showRewardedAd(show: Boolean){
        showBonificado(this, show, rewardedAd)
    }
}