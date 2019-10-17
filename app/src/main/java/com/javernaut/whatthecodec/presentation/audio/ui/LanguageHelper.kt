package com.javernaut.whatthecodec.presentation.audio.ui

import android.os.Build
import java.util.*

object LanguageHelper {
    fun getDisplayName(language: String?): String? {
        if (language == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return null
        }

        return (Locale.forLanguageTag(language)?.getDisplayLanguage(Locale.US)
                ?: language).capitalize()
    }
}