package com.javernaut.whatthecodec.compose.theme

import android.app.Activity
import android.os.Build
import android.os.PowerManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javernaut.whatthecodec.compose.common.SystemBroadcastReceiver

object WhatTheCodecTheme {
    @Composable
    fun Static(
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
    fun Dynamic(
        themeViewModel: ThemeViewModel = viewModel(),
        content: @Composable () -> Unit
    ) {
        val appTheme by themeViewModel.appTheme.collectAsState()
        val darkTheme = when (appTheme) {
            AppTheme.Light -> false
            AppTheme.Dark -> true
            AppTheme.Auto ->
                // isSystemInDarkTheme() on Android S+ considers the Battery Saver mode
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) isSystemInDarkTheme()
                else isPowerSaveMode()
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

        Static(
            darkTheme = darkTheme,
            content = content
        )
    }
}

@Composable
private fun isPowerSaveMode(): Boolean {
    val powerManager = LocalContext.current.getSystemService<PowerManager>()!!
    var result by remember {
        mutableStateOf(
            powerManager.isPowerSaveMode
        )
    }

    SystemBroadcastReceiver(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
        result = powerManager.isPowerSaveMode
    }

    return result
}
