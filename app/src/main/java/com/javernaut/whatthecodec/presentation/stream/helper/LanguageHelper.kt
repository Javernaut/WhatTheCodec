package com.javernaut.whatthecodec.presentation.stream.helper

import android.os.Build
import java.util.*

object LanguageHelper {
    fun getDisplayName(language: String?): String? {
        if (language == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return null
        }

        val capitalizedLanguage = (Locale.forLanguageTag(language)?.getDisplayLanguage(Locale.US)
                ?: language).capitalize()

        return if (capitalizedLanguage.isNotEmpty()) {
            capitalizedLanguage
        } else {
            null
        }
    }
}