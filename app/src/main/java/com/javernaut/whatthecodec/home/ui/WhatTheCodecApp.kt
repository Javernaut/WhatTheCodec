package com.javernaut.whatthecodec.home.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.javernaut.whatthecodec.home.presentation.MediaFileViewModel
import com.javernaut.whatthecodec.home.ui.navigation.HomeRoute
import com.javernaut.whatthecodec.home.ui.navigation.homeScreen
import com.javernaut.whatthecodec.settings.navigation.navigateToSettings
import com.javernaut.whatthecodec.settings.navigation.settingsScreen

@Composable
fun WhatTheCodecApp(
    viewModel: MediaFileViewModel
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        homeScreen(
            viewModel = viewModel,
            onSettingsClicked = navController::navigateToSettings
        )
        settingsScreen(onBackClick = navController::popBackStack)
    }
}
