package com.javernaut.mediafile

import android.os.Build
import java.util.Locale

object LanguageHelper {
    fun getDisplayName(language: String?): String? {
        if (language == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return null
        }

        val capitalizedLanguage = (Locale.forLanguageTag(language).getDisplayLanguage(Locale.US)
                ?: language).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        return if (capitalizedLanguage.isNotEmpty()) {
            capitalizedLanguage
        } else {
            null
        }
    }
}
