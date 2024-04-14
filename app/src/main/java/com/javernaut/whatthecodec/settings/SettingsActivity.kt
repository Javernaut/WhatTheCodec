package com.javernaut.whatthecodec.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import com.javernaut.whatthecodec.compose.theme.WhatTheCodecDynamicTheme
import com.javernaut.whatthecodec.settings.ui.SettingsScreen

// AppCompatActivity (and AppCompat theme) is used for automatic reloading when user changes a theme
// It tracks both 'local' day/night mode and a theme according to the system (auto or battery)
class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            WhatTheCodecDynamicTheme {
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
