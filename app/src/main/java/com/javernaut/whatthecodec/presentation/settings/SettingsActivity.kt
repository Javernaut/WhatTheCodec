package com.javernaut.whatthecodec.presentation.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.javernaut.whatthecodec.R
import com.javernaut.whatthecodec.presentation.compose.theme.WhatTheCodecTheme

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
        Scaffold(
            topBar = {
                SettingsTopAppBar()
            }
        ) {
            Column(Modifier.padding(it)) {
                SettingsContent()
            }
        }
    }

    @Composable
    private fun SettingsContent() {
        PreferenceTitle(title = R.string.settings_category_general)
        ThemeSelectionPreference()
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

    @Composable
    private fun SettingsTopAppBar() {
        TopAppBar(
            elevation = if (isSystemInDarkTheme()) 0.dp else AppBarDefaults.TopAppBarElevation,
            title = { Text(text = stringResource(id = R.string.settings_title)) },
            navigationIcon = {
                IconButton(onClick = { onSupportNavigateUp() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.content_description_back)
                    )
                }
            })
    }

    @Composable
    private fun ThemeSelectionPreference() {
        ListPreference(
            "theme",
            defaultValue = stringResource(id = R.string.settings_theme_default_pref_value),
            title = stringResource(id = R.string.settings_theme_title),
            displayableEntries = stringArrayResource(id = R.array.settings_theme_entries).toList(),
            entriesCodes = stringArrayResource(id = R.array.settings_theme_entryValues).toList()
        ) {
            ThemeManager.setNightModePreference(it)
        }
    }

    @Composable
    private fun OpenUrlPreference(@StringRes title: Int, @StringRes summary: Int) {
        val url = stringResource(id = summary)
        Preference(title = title, summary = summary) {
            openUrl(url)
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}
