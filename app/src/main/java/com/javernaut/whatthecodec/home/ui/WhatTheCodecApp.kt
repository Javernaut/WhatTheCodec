package com.javernaut.whatthecodec.home.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.javernaut.whatthecodec.home.presentation.MediaFileViewModel
import com.javernaut.whatthecodec.home.ui.screen.HomeScreen
import com.javernaut.whatthecodec.settings.navigation.navigateToSettings
import com.javernaut.whatthecodec.settings.navigation.settingsScreen

@Composable
fun WhatTheCodecApp(
    viewModel: MediaFileViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onSettingsClicked = navController::navigateToSettings
            )
        }
        settingsScreen(onBackClick = navController::popBackStack)
    }
}
