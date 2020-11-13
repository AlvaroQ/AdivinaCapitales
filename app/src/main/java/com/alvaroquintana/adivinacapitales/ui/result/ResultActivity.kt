package com.alvaroquintana.adivinacapitales.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.alvaroquintana.adivinacapitales.BuildConfig
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.base.BaseActivity
import com.alvaroquintana.adivinacapitales.common.startActivity
import com.alvaroquintana.adivinacapitales.ui.select.SelectActivity
import com.alvaroquintana.adivinacapitales.utils.log
import com.alvaroquintana.adivinacapitales.utils.setSafeOnClickListener
import kotlinx.android.synthetic.main.app_bar_layout.*

class ResultActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerResult, ResultFragment.newInstance())
                .commitNow()
        }

        btnBack.setSafeOnClickListener {
            startActivity<SelectActivity> {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        toolbarTitle.text = getString(R.string.resultado_screen_title)
        layoutLife.visibility = View.GONE
    }

}