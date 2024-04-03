package com.javernaut.whatthecodec.settings

import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import com.javernaut.whatthecodec.R

object ThemeManager {

    fun initNightModePreference(appContext: Context) {
        val stringNightMode = PreferenceManager.getDefaultSharedPreferences(appContext)
            .getString("theme", appContext.getString(R.string.settings_theme_default_pref_value))

        setNightModePreference(stringNightMode!!)
    }

    fun setNightModePreference(mode: String) {
        val nightMode = when (mode) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "auto" -> getAutoNightMode()
            else -> throw IllegalArgumentException("Illegal value of theme preference: $mode")
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun getAutoNightMode() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } else {
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
}
