package com.javernaut.whatthecodec.home.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.javernaut.whatthecodec.home.presentation.MediaFileViewModel
import com.javernaut.whatthecodec.home.ui.screen.HomeScreen

fun NavGraphBuilder.homeScreen(
    viewModel: MediaFileViewModel,
    onSettingsClicked: () -> Unit,
) {
    composable(route = HomeRoute) {
        HomeScreen(
            viewModel = viewModel,
            onSettingsClicked = onSettingsClicked
        )
    }
}

const val HomeRoute = "home"
