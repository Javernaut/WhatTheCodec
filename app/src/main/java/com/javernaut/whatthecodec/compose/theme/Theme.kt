package com.javernaut.whatthecodec.compose.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun WhatTheCodecTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkScheme else lightScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun WhatTheCodecDynamicTheme(
    themeViewModel: ThemeViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val appTheme by themeViewModel.appTheme.collectAsState()
    val darkTheme = when (appTheme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.Auto -> isSystemInDarkTheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    WhatTheCodecTheme(
        darkTheme = darkTheme,
        content = content
    )
}
