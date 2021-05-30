package com.javernaut.whatthecodec.presentation.stream.helper

import java.util.*

object LanguageHelper {
    fun getDisplayName(language: String?): String? {
        if (language == null) {
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
