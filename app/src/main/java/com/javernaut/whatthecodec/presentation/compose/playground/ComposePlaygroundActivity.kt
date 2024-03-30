package com.javernaut.whatthecodec.presentation.compose.playground

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.javernaut.whatthecodec.presentation.compose.theme3.WhatTheCodecM3Theme
import com.javernaut.whatthecodec.presentation.root.ui.EmptyScreen
import com.javernaut.whatthecodec.presentation.settings.SettingsScreen

class ComposePlaygroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecM3Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        EmptyScreen(
                            onVideoIconClick = {},
                            onAudioIconClick = {},
                            onSettingsClicked = {
                                navController.navigate("settings")
                            }
                        )
                    }
                    composable("settings") {
                        SettingsScreen {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}
