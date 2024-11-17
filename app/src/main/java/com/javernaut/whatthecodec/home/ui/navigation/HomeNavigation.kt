package com.javernaut.whatthecodec.home.ui.navigation

import android.content.Intent
import androidx.core.os.BundleCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.javernaut.whatthecodec.home.ui.screen.HomeScreen
import com.javernaut.whatthecodec.home.ui.screen.HomeScreenActionView

fun NavGraphBuilder.homeScreen(
    onSettingsClicked: () -> Unit,
) {
    composable(
        route = HomeRoute,
        deepLinks = listOf(
            navDeepLink {
                action = Intent.ACTION_VIEW
                mimeType = "video/*"
            },
            navDeepLink {
                action = Intent.ACTION_VIEW
                mimeType = "audio/*"
            }
        )
    ) {
        val deepLinkIntent = it.arguments?.let {
            BundleCompat.getParcelable(it, NavController.KEY_DEEP_LINK_INTENT, Intent::class.java)
        }

        val contentUri = deepLinkIntent?.data
        val mimeType = deepLinkIntent?.type

        if (contentUri != null && mimeType != null) {
            HomeScreenActionView(
                contentUri = contentUri,
                mimeType = mimeType,
                onSettingsClicked = onSettingsClicked
            )
        } else {
            HomeScreen(onSettingsClicked = onSettingsClicked)
        }
    }
}

const val HomeRoute = "home"
