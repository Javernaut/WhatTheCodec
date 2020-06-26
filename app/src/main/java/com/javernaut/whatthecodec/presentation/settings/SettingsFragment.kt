package com.javernaut.whatthecodec.presentation.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.javernaut.whatthecodec.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}