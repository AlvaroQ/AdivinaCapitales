package com.alvaroquintana.adivinacapitales.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.alvaroquintana.adivinacapitales.BuildConfig
import com.alvaroquintana.adivinacapitales.R
import com.alvaroquintana.adivinacapitales.utils.rateApp
import com.alvaroquintana.adivinacapitales.utils.shareApp

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // rate_app
        val rateApp: Preference? = findPreference("rate_app")
        rateApp?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            rateApp(requireContext())
            false
        }

        // share
        val share: Preference? = findPreference("share")
        share?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            shareApp(requireContext(), -1)
            false
        }

        // Version
        val version: Preference? = findPreference("version")
        version?.summary = "${getString(R.string.settings_version)} ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})"

        // more_apps
        val moreApps: Preference? = findPreference("more_apps")
        moreApps?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            openAppOnPlayStore()
            false
        }
    }

    private fun openAppOnPlayStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/collection/cluster?clp=igM4ChkKEzg4Nzc2MDA3NjYwNDEzMDc4NTIQCBgDEhkKEzg4Nzc2MDA3NjYwNDEzMDc4NTIQCBgDGAA%3D:S:ANO1ljItPd0&gsr=CjuKAzgKGQoTODg3NzYwMDc2NjA0MTMwNzg1MhAIGAMSGQoTODg3NzYwMDc2NjA0MTMwNzg1MhAIGAMYAA%3D%3D:S:ANO1ljLjm34")))
        } catch (notFoundException: ActivityNotFoundException) {

        }
    }
}