package com.javernaut.whatthecodec.presentation.compose.playground

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.javernaut.whatthecodec.presentation.compose.theme3.WhatTheCodecM3Theme
import com.javernaut.whatthecodec.presentation.root.ui.EmptyScreen
import com.javernaut.whatthecodec.presentation.settings.SettingsActivity

class ComposePlaygroundActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecM3Theme {
                EmptyScreen(
                    onVideoIconClick = {},
                    onAudioIconClick = {},
                    onSettingsClicked = {
                        SettingsActivity.start(this)
                    }
                )
            }
        }
    }
}
