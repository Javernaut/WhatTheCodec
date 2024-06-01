package com.javernaut.whatthecodec.feature.settings.api.theme

import kotlinx.coroutines.flow.Flow

interface ThemeSettingsRepository {
    val themeSettings: Flow<ThemeSettings>

    suspend fun setSelectedTheme(newAppTheme: AppTheme)
}
