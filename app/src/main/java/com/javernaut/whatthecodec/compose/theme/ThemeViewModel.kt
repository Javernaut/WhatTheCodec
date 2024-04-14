package com.javernaut.whatthecodec.compose.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {

    private val _appTheme = MutableStateFlow(AppTheme.Light)
    val appTheme = _appTheme.asStateFlow()

    fun setAppTheme(newAppTheme: AppTheme) {
        _appTheme.value = newAppTheme
    }
}
