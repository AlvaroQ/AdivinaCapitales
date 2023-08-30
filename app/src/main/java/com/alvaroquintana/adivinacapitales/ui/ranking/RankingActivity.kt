package com.alvaroquintana.adivinacapitales.ui.ranking

import android.os.Bundle
import android.view.View
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.base.BaseActivity
import com.alvaroquintana.adivinacapitales.common.viewBinding
import com.alvaroquintana.adivinacapitales.databinding.RankingActivityBinding
import com.alvaroquintana.adivinacapitales.utils.setSafeOnClickListener

class RankingActivity : BaseActivity() {
    private val binding by viewBinding(RankingActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerResult, RankingFragment.newInstance())
                .commitNow()
        }

        binding.appBar.btnBack.setSafeOnClickListener {
            finish()
        }
        binding.appBar.toolbarTitle.text = getString(R.string.ranking_screen_title)
        binding.appBar.layoutLife.visibility = View.GONE
    }
}