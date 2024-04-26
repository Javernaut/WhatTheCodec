package com.javernaut.whatthecodec.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.javernaut.whatthecodec.settings.ui.SettingsScreen

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(SettingsRoute, navOptions)

fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit
) {
    composable(route = SettingsRoute) {
        SettingsScreen(onBackClick = onBackClick)
    }
}

private const val SettingsRoute = "settings"
