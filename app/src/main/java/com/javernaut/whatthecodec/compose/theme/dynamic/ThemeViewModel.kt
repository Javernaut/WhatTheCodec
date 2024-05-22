package com.javernaut.whatthecodec.compose.theme.dynamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javernaut.whatthecodec.feature.settings.theme.AppTheme
import com.javernaut.whatthecodec.settings.presentation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val appThemeRepository: AppThemeRepository
) : ViewModel() {

    val appTheme = appThemeRepository.themeSettings
        .map { it.selectedTheme }
        .stateIn(AppTheme.Auto)

    fun setAppTheme(newAppTheme: AppTheme) {
        viewModelScope.launch {
            appThemeRepository.setSelectedTheme(newAppTheme)
        }
    }
}
