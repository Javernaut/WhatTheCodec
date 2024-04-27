package com.javernaut.whatthecodec.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun MaterialNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit
) {
    val defaultSlideDistance = rememberSlideDistance()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        contentAlignment = contentAlignment,
        route = route,
        enterTransition = {
            materialSharedAxisXIn(true, defaultSlideDistance)
        },
        exitTransition = {
            materialSharedAxisXOut(true, defaultSlideDistance)
        },
        popEnterTransition = {
            materialSharedAxisXIn(false, defaultSlideDistance)
        },
        popExitTransition = {
            materialSharedAxisXOut(false, defaultSlideDistance)
        },
        builder = builder
    )
}
