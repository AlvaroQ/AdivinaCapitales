package com.alvaroquintana.adivinacapitales.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.base.BaseActivity
import com.alvaroquintana.adivinacapitales.common.startActivity
import com.alvaroquintana.adivinacapitales.common.viewBinding
import com.alvaroquintana.adivinacapitales.databinding.ResultActivityBinding
import com.alvaroquintana.adivinacapitales.ui.select.SelectActivity
import com.alvaroquintana.adivinacapitales.utils.setSafeOnClickListener

class ResultActivity : BaseActivity() {
    private val binding by viewBinding(ResultActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerResult, ResultFragment.newInstance())
                .commitNow()
        }

        binding.appBar.btnBack.setSafeOnClickListener {
            startActivity<SelectActivity> {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        binding.appBar.toolbarTitle.text = getString(R.string.resultado_screen_title)
        binding.appBar.layoutLife.visibility = View.GONE
    }

}