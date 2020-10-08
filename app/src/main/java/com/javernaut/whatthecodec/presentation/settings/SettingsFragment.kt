package com.javernaut.whatthecodec.presentation.settings

import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.javernaut.whatthecodec.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        findPreference<ListPreference>("theme")?.setOnPreferenceChangeListener { _, newValue ->
            ThemeManager.setNightModePreference(newValue.toString())
            true
        }

        arrayOf("source_code", "privacy_policy").forEach {
            findPreference<Preference>(it)?.setOnPreferenceClickListener { pref: Preference ->
                openUrl(pref.summary.toString())
                true
            }
        }
    }

    private fun openUrl(url: String) {
        CustomTabsIntent.Builder()
                .build()
                .launchUrl(requireContext(), Uri.parse(url))
    }
}
