package com.javernaut.whatthecodec.feature.settings.impl.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.javernaut.whatthecodec.feature.settings.theme.AppTheme
import com.javernaut.whatthecodec.feature.settings.theme.ThemeSettings
import com.javernaut.whatthecodec.feature.settings.theme.ThemeSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultThemeSettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) : ThemeSettingsRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")
    private val keyTheme = intPreferencesKey("selected_theme")

    private val dataStore = context.dataStore

    private val selectedTheme: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            preferences[keyTheme] ?: AppTheme.Auto.ordinal
        }.map {
            AppTheme.entries[it]
        }

    override val themeSettings = selectedTheme.map { ThemeSettings(it) }

    override suspend fun setSelectedTheme(newAppTheme: AppTheme) {
        dataStore.edit { settings ->
            settings[keyTheme] = newAppTheme.ordinal
        }
    }
}
