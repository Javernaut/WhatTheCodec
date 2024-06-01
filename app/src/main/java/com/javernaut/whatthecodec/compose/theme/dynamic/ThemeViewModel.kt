package com.javernaut.whatthecodec.compose.theme.dynamic

import androidx.lifecycle.ViewModel
import com.javernaut.whatthecodec.feature.settings.api.theme.AppTheme
import com.javernaut.whatthecodec.feature.settings.api.theme.ThemeSettingsRepository
import com.javernaut.whatthecodec.settings.presentation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    themeSettingsRepository: ThemeSettingsRepository
) : ViewModel() {

    val appTheme = themeSettingsRepository.themeSettings
        .map { it.selectedTheme }
        .stateIn(AppTheme.Auto)
}
