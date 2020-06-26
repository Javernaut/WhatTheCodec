package com.javernaut.whatthecodec

import android.app.Application
import com.javernaut.whatthecodec.presentation.settings.ThemeManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ThemeManager.initNightModePreference(this)
    }

}