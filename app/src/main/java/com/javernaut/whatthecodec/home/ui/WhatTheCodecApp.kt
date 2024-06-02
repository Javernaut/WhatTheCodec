package com.javernaut.whatthecodec.home.ui

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.javernaut.whatthecodec.compose.navigation.MaterialNavHost
import com.javernaut.whatthecodec.feature.settings.navigation.navigateToSettings
import com.javernaut.whatthecodec.feature.settings.navigation.settingsScreen
import com.javernaut.whatthecodec.home.ui.navigation.HomeRoute
import com.javernaut.whatthecodec.home.ui.navigation.homeScreen

@Composable
fun WhatTheCodecApp() {
    val navController = rememberNavController()
    MaterialNavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    ) {
        homeScreen(
            onSettingsClicked = navController::navigateToSettings
        )
        settingsScreen(
            onBackClick = navController::popBackStack
        )
    }
}
