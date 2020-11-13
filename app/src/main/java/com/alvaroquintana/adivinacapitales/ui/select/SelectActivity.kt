package com.alvaroquintana.adivinacapitales.ui.select

import android.os.Bundle
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.base.BaseActivity

class SelectActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerSelect, SelectFragment.newInstance())
                .commitNow()
        }
    }
}