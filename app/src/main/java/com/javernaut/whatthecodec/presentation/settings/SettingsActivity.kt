package com.javernaut.whatthecodec.presentation.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import com.javernaut.whatthecodec.presentation.compose.theme3.WhatTheCodecM3Theme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecM3Theme {
                // TODO Navigate up with nav controller instead
                SettingsScreen(::openUrl, ::finish)
            }
        }
    }

    private fun openUrl(url: String) {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(this, Uri.parse(url))
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}
