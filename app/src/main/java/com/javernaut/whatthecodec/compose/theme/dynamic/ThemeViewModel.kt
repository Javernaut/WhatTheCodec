package com.javernaut.whatthecodec.compose.theme.dynamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val appThemeRepository: AppThemeRepository
) : ViewModel() {

    val appTheme = appThemeRepository.selectedTheme.stateIn(
        viewModelScope, SharingStarted.Eagerly, AppTheme.Auto
    )

    fun setAppTheme(newAppTheme: AppTheme) {
        viewModelScope.launch {
            appThemeRepository.setSelectedTheme(newAppTheme)
        }
    }
}
