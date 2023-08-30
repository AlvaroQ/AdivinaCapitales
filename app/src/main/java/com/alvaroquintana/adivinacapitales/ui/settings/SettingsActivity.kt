package com.alvaroquintana.adivinacapitales.ui.settings

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.base.BaseActivity
import com.alvaroquintana.adivinacapitales.common.viewBinding
import com.alvaroquintana.adivinacapitales.databinding.SettingsActivityBinding
import com.alvaroquintana.adivinacapitales.utils.setSafeOnClickListener


class SettingsActivity : BaseActivity() {
    private val binding by viewBinding(SettingsActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        binding.appBar.toolbarTitle.text = getString(R.string.settings)
        binding.appBar.layoutLife.visibility = View.GONE
        binding.appBar.btnBack.setSafeOnClickListener { finishAfterTransition() }
    }
}