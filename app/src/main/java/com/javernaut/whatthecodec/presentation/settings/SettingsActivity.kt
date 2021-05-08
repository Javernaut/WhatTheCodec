package com.javernaut.whatthecodec.presentation.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme
import com.javernaut.whatthecodec.presentation.compose.theme.secondaryText

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContent {
            WhatTheCodecTheme {
                SettingsScreen()
            }
        }
    }

    private fun openUrl(url: String) {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(this, Uri.parse(url))
    }

    @Composable
    fun SettingsScreen() {
        Column {
            PreferenceTitle(title = R.string.settings_category_general)
            // TODO App theme
            PreferenceDivider()
            PreferenceTitle(title = R.string.settings_category_about)
            OpenUrlPreference(
                title = R.string.settings_source_code_title,
                summary = R.string.settings_source_code_summary
            )
            OpenUrlPreference(
                title = R.string.settings_privacy_policy_title,
                summary = R.string.settings_privacy_policy_summary
            )
        }
    }

    @Composable
    private fun PreferenceTitle(@StringRes title: Int) {
        Text(
            stringResource(id = title),
            Modifier
                .fillMaxWidth()
                .padding(start = 72.dp, top = 24.dp, end = 8.dp, bottom = 8.dp),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.secondary
        )
    }

    @Composable
    private fun Preference(
        @StringRes title: Int,
        @StringRes summary: Int,
        clickHandler: () -> Unit
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = clickHandler)
                .padding(
                    start = 72.dp, top = 16.dp, end = 8.dp, bottom = 16.dp
                )
        ) {
            Text(
                stringResource(id = title),
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                stringResource(id = summary),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondaryText
            )
        }
    }

    @Composable
    private fun OpenUrlPreference(@StringRes title: Int, @StringRes summary: Int) {
        val url = stringResource(id = summary)
        Preference(title = title, summary = summary) {
            openUrl(url)
        }
    }

    @Composable
    private fun PreferenceDivider() {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0x1f000000))
        )
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}
